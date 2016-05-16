from srilm import LM
import sqlite3


def _check_fit(values, prob):
    for (idx, p) in values:
        if prob > p:
            return True
    return False


def _remove_min(values):
    min_tuple = min(values, key=lambda t: t[1])
    values.remove(min_tuple)


class NGramPredictor:
    def __init__(self, path_to_lm):
        self.lm = LM(path_to_lm, lower=False)

    def next_word_prediction(self, words, best_num):
        context = [self.lm.vocab.intern(word) for word in words]
        result = []
        for i in xrange(self.lm.vocab.max_interned() + 1):
            logprob = self.lm.logprob(i, context)
            if len(result) < best_num:
                result.append((self.lm.vocab.extern(i), logprob))
            elif _check_fit(result, logprob):
                _remove_min(result)
                result.append((self.lm.vocab.extern(i), logprob))
        result = map(lambda (a, b): (a, 10**b), result)
        return sorted(result, key=lambda t: t[1], reverse=True)

    def sentence_score_prediction(self, sentences):
        result = []
        for sentence in sentences:
            logprob = self.lm.total_logprob_strings(sentence)
            result.append((sentence, logprob))
        return map(lambda (a, b): (a, 10**b), result)


class BigramPredictor:
    def __init__(self, sqllitedb_path):
        self.conn = sqlite3.connect(sqllitedb_path)

    def _store_bigrams(self, path):
        cursor = self.conn.cursor()
        key_bigram = '2-grams:'
        in_bigram_section = False
        with open(path, 'r+') as f:
            for line in f:
                if key_bigram in line:
                    in_bigram_section = True
                if in_bigram_section:
                    splits = line.split()
                    if len(splits) is not 3:
                        continue
                    else:
                        cursor.execute('INSERT INTO bigrams VALUES (?,?)', [splits[1], splits[2]])
        self.conn.commit()

    def store_birams(self, path_lm):
        c = self.conn.cursor()
        c.execute('''DROP TABLE IF EXISTS bigrams''')
        c.execute('''CREATE TABLE bigrams
             (bileft text, biright text)''')
        c.execute('''CREATE INDEX index_bileft
              ON bigrams (bileft);''')
        self.conn.commit()
        self._store_bigrams(path_lm)

    def get_candidates_for_word(self, word):
        c = self.conn.cursor()
        res = []
        for row in c.execute('SELECT biright FROM bigrams WHERE bileft=?', (word,)):
            if len(row) > 0:
                res.append(row[0])
        return res

    def close(self):
        self.conn.close()
