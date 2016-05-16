import pickle
import re

TOKEN_START = "<s>"
TOKEN_END = "</s>"
TOKEN_UNKOWN = "<unk>"


class Vocabulary:
    def __init__(self):
        self.max_sent_len = 0
        self.vocab_to_count = {}
        self.num_hist = 0

    def process(self, path):
        with open(path, "r") as f:
            last_line = ""
            for line in f:
                if line in last_line:
                    continue
                last_line = line
                self.num_hist += 1
                tokens = line.split()
                if len(tokens) > self.max_sent_len:
                    self.max_sent_len = len(tokens)
                for word in tokens:
                    token = word
                    if token in self.vocab_to_count:
                        count = self.vocab_to_count[token]
                        count += 1
                        self.vocab_to_count[token] = count
                    else:
                        self.vocab_to_count[token] = 1

    def get_vocab_size(self):
        return len(self.vocab_to_count)

    def get_max_sent_len(self):
        return self.max_sent_len

    def get_word_to_id(self, max_vocab_size):
        max_vocab = max_vocab_size - 3
        items = sorted(self.vocab_to_count.items(), key=lambda t: t[1], reverse=True)
        vocab = [x[0] for x in items[:max_vocab]]
        vocab.append(TOKEN_START)
        vocab.append(TOKEN_END)
        vocab.append(TOKEN_UNKOWN)
        vocab.sort()
        return vocab, dict(zip(vocab, range(len(vocab))))

    def gen_data(self, max_vocab_size, out_path="./out.p"):
        vocab, word_to_id = self.get_word_to_id(max_vocab_size)
        pickle.dump([self.max_sent_len, word_to_id, vocab], open(out_path, "wb"))


def create_train(file, num_train, vocab, out_train="./data/train.txt", out_eval="./data/val.txt"):
    with open(file, "r") as r_f, open(out_train, "w") as w_train, open(out_eval, "w") as w_eval:
        last_line = " "
        line_count = 0
        for line in r_f:
            if line in last_line:
                continue
            line_count += 1
            last_line = line
            tokens = line.split()
            new_line = []
            for token in tokens:
                if token in vocab:
                    new_line.append(token)
                else:
                    new_line.append(TOKEN_UNKOWN)

            line_str = ' '.join(new_line)
            if line_count > num_train:
                w_eval.write("%s\n" % line_str)
            else:
                w_train.write("%s\n" % line_str)
