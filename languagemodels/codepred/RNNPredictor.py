"""Implementation of a RNN network
    based on the Tutorial
    https://www.tensorflow.org/versions/r0.8/tutorials/recurrent/index.html#recurrent-neural-networks
    which implements model of (Zaremba, et. al.) Recurrent Neural Network Regularization
    http://arxiv.org/abs/1409.2329

    Similar model described in:
    (Zaremba, et. al.) Recurrent Neural Network Regularization
    http://arxiv.org/abs/1409.2329

    Paramters from Tutorial
    The hyperparameters used in the model:
    - init_scale - the initial scale of the weights
    - learning_rate - the initial value of the learning rate
    - max_grad_norm - the maximum permissible norm of the gradient
    - num_layers - the number of LSTM layers
    - num_steps - the number of unrolled steps of LSTM
    - hidden_size - the number of LSTM units
    - max_epoch - the number of epochs trained with the initial learning rate
    - max_max_epoch - the total number of epochs for training
    - keep_prob - the probability of keeping weights in the dropout layer
    - lr_decay - the decay of the learning rate for each epoch after "max_epoch"
    - batch_size - the batch size

    -l2_reg_lambda - the influence of the l2 regularization (see cost function)
"""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import time
import collections
import os

import numpy as np
import tensorflow as tf
import codepred.Vocabulary as pre
from random import shuffle
import itertools


class APIPredModel(object):
    """
    Implememnts the LSTM-RNN model for API prdiction in tensorflow
    """

    def __init__(self, is_training, config):
        """
        Creates the tensorflow graph for the LSTM-RNN
        :param is_training: bool if the model is used for training
        :param config: the parameter config
        :return:
        """
        self.batch_size = batch_size = config.batch_size
        self.num_steps = num_steps = config.num_steps
        size = config.hidden_size
        vocab_size = config.vocab_size

        self.input_data = tf.placeholder(tf.int32, [batch_size, num_steps])
        self.targets = tf.placeholder(tf.int32, [batch_size, num_steps])

        lstm_cell = tf.nn.rnn_cell.BasicLSTMCell(size, forget_bias=0.0)
        if is_training and config.keep_prob < 1:
            lstm_cell = tf.nn.rnn_cell.DropoutWrapper(
                lstm_cell, output_keep_prob=config.keep_prob)
        cell = tf.nn.rnn_cell.MultiRNNCell([lstm_cell] * config.num_layers)

        # init state is zero
        self.initial_state = cell.zero_state(batch_size, tf.float32)

        # word embedding
        with tf.device("/cpu:0"):
            embedding = tf.get_variable("embedding", [vocab_size, size])
            inputs = tf.nn.embedding_lookup(embedding, self.input_data)

        # add dopout layer if training
        if is_training and config.keep_prob < 1:
            inputs = tf.nn.dropout(inputs, config.keep_prob)

        inputs = [tf.squeeze(input_, [1])
                  for input_ in tf.split(1, num_steps, inputs)]
        # arrange LSTM rnn cells
        outputs, state = tf.nn.rnn(cell, inputs, initial_state=self.initial_state)

        # perform softmax regression on outputs from LSTM cells
        output = tf.reshape(tf.concat(1, outputs), [-1, size])
        softmax_w = tf.get_variable("softmax_w", [size, vocab_size])
        softmax_b = tf.get_variable("softmax_b", [vocab_size])
        # add l2 regulation, avoid large values
        l2_reg = tf.constant(0.0)
        logits = tf.nn.xw_plus_b(output, softmax_w, softmax_b)
        loss = tf.nn.seq2seq.sequence_loss_by_example(
            [logits],
            [tf.reshape(self.targets, [-1])],
            [tf.ones([batch_size * num_steps])])
        l2_reg += tf.nn.l2_loss(softmax_w)
        l2_reg += tf.nn.l2_loss(softmax_b)
        # add l2 reg to cost dunction
        self.cost = cost = tf.reduce_sum(loss) / batch_size + config.l2_reg_lambda * l2_reg
        self.final_state = state

        self.probabilities = tf.nn.softmax(logits)
        self.logits = logits

        if not is_training:
            return

        self.lr = tf.Variable(0.0, trainable=False)
        tvars = tf.trainable_variables()
        grads, _ = tf.clip_by_global_norm(tf.gradients(cost, tvars),
                                          config.max_grad_norm)
        if config.use_adam_optimizer:
            optimizer = tf.train.AdamOptimizer(self.lr)
        else:
            optimizer = tf.train.GradientDescentOptimizer(self.lr)
        self.train_op = optimizer.apply_gradients(zip(grads, tvars))

    def assign_lr(self, session, lr_value):
        session.run(tf.assign(self.lr, lr_value))


class APIPredictionConfig(object):
    """
    Config for API Prediction
    good params?: http://dl.acm.org/citation.cfm?id=2876379&dl=ACM&coll=DL
    """
    def __init__(self):
        self.init_scale = 0.05
        self.learning_rate = 1.0
        self.max_grad_norm = 5
        self.num_layers = 2
        self.num_steps = 35
        #self.hidden_size = 650
        self.hidden_size = 400
        self.max_epoch = 2
        self.max_max_epoch = 20
        self.keep_prob = 0.5
        self.lr_decay = 0.8
        self.batch_size = 20
        self.vocab_size = 20000
        self.l2_reg_lambda = 1e-4
        self.use_adam_optimizer = False


def _run_epoch(session, m, data, eval_op, verbose=False):
    epoch_size = ((len(data) // m.batch_size) - 1) // m.num_steps
    start_time = time.time()
    costs = 0.0
    iters = 0
    state = m.initial_state.eval()
    for step, (x, y) in enumerate(file_iterator(data, m.batch_size,
                                                m.num_steps)):
        cost, state, _ = session.run([m.cost, m.final_state, eval_op],
                                     {m.input_data: x,
                                      m.targets: y,
                                      m.initial_state: state})
        costs += cost
        iters += m.num_steps

        if verbose and step % (epoch_size // 10) == 10:
            print("%.3f perplexity: %.3f speed: %.0f wps" %
                  (step * 1.0 / epoch_size, np.exp(costs / iters),
                   iters * m.batch_size / (time.time() - start_time)))
    return np.exp(costs / iters)


def train_model(train_path, test_path, model_store_dir, model_name,
                config=APIPredictionConfig(), vocab_path="./models/vocab.p", restore=False, restore_epoch=0):
    """
    Train the LSTM-RNN
    :param train_path: path of the training data
    :param test_path: path of the validation data
    :param model_store_dir: path to store the model in
    :param model_name: the name of the model
    :param config: the parameters for the LSTM-RNN
    :param vocab_path: the path to the vocab pickle file produced by the preprocessor
    :param restore: indicates if the training should restor a previous state (optional)
    :param restore_epoch: the epoch to restore (optional)
    :return:
    """
    [_, word_to_id, vocab] = pre.load_vocab_data(path=vocab_path)
    train_data, test_data = _raw_data(train_path, test_path, word_to_id)
    config.vocab_size = len(vocab)

    train_time = 0

    print("Start Training")
    with tf.Graph().as_default(), tf.Session() as session:
        initializer = tf.random_uniform_initializer(-config.init_scale,
                                                    config.init_scale)
        with tf.variable_scope("model", reuse=None, initializer=initializer):
            m = APIPredModel(is_training=True, config=config)
        with tf.variable_scope("model", reuse=True, initializer=initializer):
            mvalid = APIPredModel(is_training=False, config=config)

        tf.initialize_all_variables().run()
        saver = tf.train.Saver(tf.all_variables())
        last_test_perplexity = -1
        num_increase_in_row = 0
        max_num_increase = 1
        if restore:
            ckpt = tf.train.get_checkpoint_state(model_store_dir)
            if ckpt and ckpt.model_checkpoint_path:
                saver.restore(session, ckpt.model_checkpoint_path)
            else:
                 raise RuntimeError("Model not found")

        for i in range(restore_epoch, config.max_max_epoch):
            lr_decay = config.lr_decay ** max(i - config.max_epoch, 0.0)
            m.assign_lr(session, config.learning_rate * lr_decay)

            start_time = time.time()
            print("Epoch: %d Learning rate: %.3f" % (i + 1, session.run(m.lr)))
            train_perplexity = _run_epoch(session, m, train_data, m.train_op,
                                            verbose=True)
            end_time = time.time()
            epoch_time = (end_time-start_time)
            train_time += epoch_time
            save_path = saver.save(session, os.path.join(model_store_dir, model_name + ".ckpt"), global_step=(i+1))
            print("Model saved in file: %s" % save_path)
            print("Epoch: %d Train Perplexity: %.3f" % (i + 1, train_perplexity))
            print("Epoch duration: %d, total duration: %d" % (epoch_time, train_time))

            test_perplexity = _run_epoch(session, mvalid, test_data, tf.no_op())
            print("Valid Perplexity: %.3f" % test_perplexity)

            if last_test_perplexity < 0:
                last_test_perplexity = test_perplexity
            else:
                if last_test_perplexity < test_perplexity:
                    num_increase_in_row += 1
                else:
                    num_increase_in_row = 0
                last_test_perplexity = test_perplexity
            if num_increase_in_row >= max_num_increase:
                print("%d times test perplexity increased -> stop training" % num_increase_in_row)
                break
    print("Training Finished after: %f seconds" % train_time)


def get_perplexity(train_path, test_path, model_store_dir,
                   config=APIPredictionConfig(), vocab_path="./models/vocab.p"):
    [_, word_to_id, vocab] = pre.load_vocab_data(path=vocab_path)
    train_data, test_data = _raw_data(train_path, test_path, word_to_id)
    config.vocab_size = len(vocab)

    print("Start Perplexity measure")
    with tf.Graph().as_default(), tf.Session() as session:
        with tf.variable_scope("model", reuse=False):
            mvalid = APIPredModel(is_training=False, config=config)

        tf.initialize_all_variables().run()
        saver = tf.train.Saver(tf.all_variables())
        ckpt = tf.train.get_checkpoint_state(model_store_dir)
        if ckpt and ckpt.model_checkpoint_path:
            saver.restore(session, ckpt.model_checkpoint_path)
        else:
             raise RuntimeError("Model not found")

        test_perplexity = _run_epoch(session, mvalid, test_data, tf.no_op())
        print("Valid Perplexity: %.3f" % test_perplexity)


class LSTMPredictor:
    """
    Used for querying the LSTM-RNN model
    !Needs to be closed after usage
    """
    def __init__(self, save_dir, vocab_path="./models/vocab.p", eval_config=APIPredictionConfig()):
        """
        Init LSTM Predictor
        :param save_dir: the path to the LSTM-RNN model
        :param vocab_path: the path to the pickle vocab file
        :param eval_config: (optional) the config of the LSTM-RNN model
        :return:
        """
        self.eval_config = eval_config
        [_, word_to_id, vocab] = pre.load_vocab_data(path=vocab_path)
        self.words = vocab
        self.vocab = word_to_id
        self.eval_config.vocab_size = len(self.words)
        self.eval_config.batch_size = 1
        self.eval_config.num_steps = 1

        with tf.Graph().as_default():
            self.session = tf.Session()
            with self.session.as_default():
                with tf.variable_scope("model", reuse=False):
                    self.model = APIPredModel(is_training=False, config=eval_config)

                tf.initialize_all_variables().run()
                saver = tf.train.Saver(tf.all_variables())
                ckpt = tf.train.get_checkpoint_state(save_dir)
                if ckpt and ckpt.model_checkpoint_path:
                    saver.restore(self.session, ckpt.model_checkpoint_path)
                else:
                    raise RuntimeError("Model not found")

    def close(self):
        self.session.close()

    def sentence_score_prediction(self, sentences):
        """
        Computes the scores for each sentence in sentences
        :param sentences: list of sentences
        :return: list of scores
        """
        with self.session.as_default():
            result = []
            for sentence in sentences:
                sent_split = sentence.split()
                prob = 1.0
                state = self.model.initial_state.eval()
                for word in range(len(sent_split)-1):
                    x = np.zeros((1, 1))
                    x[0, 0] = self.vocab[sent_split[word]]
                    feed = {self.model.input_data: x, self.model.initial_state: state}
                    [probs, state] = self.session.run([self.model.probabilities, self.model.final_state], feed)
                    n_word_index = self.vocab[sent_split[word+1]]
                    p = probs[0]
                    prob *= p[n_word_index]
                result.append((sentence, prob))
            return result

    def score_complentions(self, contexts):
        """
        Given the contexts computes scores for contuation
        :param contexts: list of contexts as list of words
        :return: list of scores for all words
        """
        num_words = len(self.words)
        scores = np.zeros((num_words, len(contexts)))
        for step, context in enumerate(contexts):
            with self.session.as_default():
                state = self.model.initial_state.eval()
                for word in context[:-1]:
                    x = np.zeros((1, 1))
                    x[0, 0] = self.vocab[word]
                    feed = {self.model.input_data: x, self.model.initial_state: state}
                    [state] = self.session.run([self.model.final_state], feed)

                last = context[-1]
                x = np.zeros((1, 1))
                x[0, 0] = self.vocab[last]
                feed = {self.model.input_data: x, self.model.initial_state: state}
                [probs, _] = self.session.run([self.model.probabilities, self.model.final_state], feed)
                scores[:, step] = probs[0]
        final_scores = np.mean(scores, axis=1)
        return zip(self.words, final_scores)

    def get_context_prop(self, context, candidates):
        """
        Get probabilities for a given context with possible candodates
        :param context: the context as list of words
        :param candidates: list of candidates
        :return: list of scores for candi
        """
        with self.session.as_default():
            state = self.model.initial_state.eval()
            for word in context[:-1]:
                x = np.zeros((1, 1))
                x[0, 0] = self.vocab[word]
                feed = {self.model.input_data: x, self.model.initial_state: state}
                [state] = self.session.run([self.model.final_state], feed)

            last = context[-1]
            x = np.zeros((1, 1))
            x[0, 0] = self.vocab[last]
            feed = {self.model.input_data: x, self.model.initial_state: state}
            [probs, _] = self.session.run([self.model.probabilities, self.model.final_state], feed)
            probs_context = probs[0]
            res = [probs_context[self.vocab[cand]] for cand in candidates]
            return res

    def next_word_prediction(self, sentence, num_best):
        """
        Given a sentence provides a listof num_best predictions for continuations of the sentence
        :param sentence: the sentence as string
        :param num_best: returns top num_best predictions
        :return: list of continuation words
        """
        with self.session.as_default():
            state = self.model.initial_state.eval()
            sent_split = sentence.split()
            for word in sent_split[:-1]:
                x = np.zeros((1, 1))
                x[0, 0] = self.vocab[word]
                feed = {self.model.input_data: x, self.model.initial_state: state}
                [state] = self.session.run([self.model.final_state], feed)

            last = sent_split[-1]
            x = np.zeros((1, 1))
            x[0, 0] = self.vocab[last]
            feed = {self.model.input_data: x, self.model.initial_state: state}
            [probs, _] = self.session.run([self.model.probabilities, self.model.final_state], feed)
            p = probs[0]
            word_and_prob = zip(self.words, p)
            res = sorted(word_and_prob, key=lambda tup: tup[1], reverse=True)
            return res[0:num_best]


# Slightly changed reader functions from the Tutorial


def _read_words(filename):
    res = []
    with open(filename, "r") as f:
        for line in f:
            tokens = line.split()
            cur = list()
            cur.append(pre.TOKEN_START)
            cur.extend(tokens)
            cur.append(pre.TOKEN_END)
            res.append(cur)
    shuffle(res)
    return list(itertools.chain(*res))


def word_to_id_from_vocab(words):
    return dict(zip(words, range(len(words))))


def _build_vocab(filename):
    data = _read_words(filename)

    counter = collections.Counter(data)
    count_pairs = sorted(counter.items(), key=lambda x: (-x[1], x[0]))

    words, _ = list(zip(*count_pairs))
    word_to_id = word_to_id_from_vocab(words)

    return word_to_id, words


def _file_to_word_ids(filename, word_to_id):
    data = _read_words(filename)
    id_unk = word_to_id[pre.TOKEN_UNKOWN]
    return [word_to_id.get(word, id_unk) for word in data]


def _raw_data(train_path, test_path, word_to_id):
    train_data = _file_to_word_ids(train_path, word_to_id)
    test_data = _file_to_word_ids(test_path, word_to_id)
    return train_data, test_data


def file_iterator(raw_data, batch_size, num_steps):
    raw_data = np.array(raw_data, dtype=np.int32)

    data_len = len(raw_data)
    batch_len = data_len // batch_size
    data = np.zeros([batch_size, batch_len], dtype=np.int32)
    for i in range(batch_size):
        data[i] = raw_data[batch_len * i:batch_len * (i + 1)]

    epoch_size = (batch_len - 1) // num_steps

    if epoch_size == 0:
        raise ValueError("epoch_size == 0, decrease batch_size or num_steps")

    for i in range(epoch_size):
        x = data[:, i * num_steps:(i + 1) * num_steps]
        y = data[:, i * num_steps + 1:(i + 1) * num_steps + 1]
        yield (x, y)
