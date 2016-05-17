"""Implementation of a RNN network
 based on the Tutorial
 https://www.tensorflow.org/versions/r0.8/tutorials/recurrent/index.html#recurrent-neural-networks
 which implements model of (Zaremba, et. al.) Recurrent Neural Network Regularization
 http://arxiv.org/abs/1409.2329

Trains the model described in:
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
import pickle
import os

import numpy as np
import tensorflow as tf
from tensorflow.models.rnn import rnn
import codepred.Vocabulary as pre


class APIPredModel(object):
    """The APIPred model."""

    def __init__(self, is_training, config):
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

        self.initial_state = cell.zero_state(batch_size, tf.float32)

        with tf.device("/cpu:0"):
            embedding = tf.get_variable("embedding", [vocab_size, size])
            inputs = tf.nn.embedding_lookup(embedding, self.input_data)

        if is_training and config.keep_prob < 1:
            inputs = tf.nn.dropout(inputs, config.keep_prob)

        inputs = [tf.squeeze(input_, [1])
                  for input_ in tf.split(1, num_steps, inputs)]
        outputs, state = rnn.rnn(cell, inputs, initial_state=self.initial_state)

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
        self.max_epoch = 6
        self.max_max_epoch = 39
        self.keep_prob = 0.5
        self.lr_decay = 0.8
        self.batch_size = 20
        self.vocab_size = 10000
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
    [_, word_to_id, vocab] = pre.load_vocab_data(path=vocab_path)
    train_data, test_data = _raw_data(train_path, test_path, word_to_id)
    config.vocab_size = len(vocab)

    eval_config = APIPredictionConfig()
    eval_config.vocab_size = len(vocab)
    eval_config.batch_size = 1
    eval_config.num_steps = 1

    train_time = 0

    print("Start Training")
    with tf.Graph().as_default(), tf.Session() as session:
        initializer = tf.random_uniform_initializer(-config.init_scale,
                                                    config.init_scale)
        with tf.variable_scope("model", reuse=None, initializer=initializer):
            m = APIPredModel(is_training=True, config=config)
        with tf.variable_scope("model", reuse=True, initializer=initializer):
            mtest = APIPredModel(is_training=False, config=eval_config)

        tf.initialize_all_variables().run()
        saver = tf.train.Saver(tf.all_variables())
        if restore:
            ckpt = tf.train.get_checkpoint_state(train_path)
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

            test_perplexity = _run_epoch(session, mtest, test_data, tf.no_op())
            print("Test Perplexity: %.3f" % test_perplexity)
    print("Training Finished after: %f seconds" % train_time)


class LSTMPredictor:
    def __init__(self, save_dir, vocab_path="./models/vocab.p", eval_config=APIPredictionConfig()):
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
            res.append(pre.TOKEN_START)
            res.extend(tokens)
            res.append(pre.TOKEN_END)
    return res


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
