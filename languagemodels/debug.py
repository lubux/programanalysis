from codepred.Vocabulary import Vocabulary
import codepred.Vocabulary as pre
import pred_funcs as pred
from codepred.NGRAMPredictor import BigramPredictor


data_file = "./data/train.txt"

#prep = Vocabulary()
#prep.process(data_file)
#prep.gen_data(10000)

#num_eval = prep.num_hist // 4
#pre.create_train(data_file, prep.num_hist-num_eval, prep.vocab_to_count)

bigram = BigramPredictor("./models/ngram_lm2db")
bigram.store_birams("./models/ngram_lm2")

[max_sent_len, word_to_id, vocab] = pre.load_vocab_data()

print "%d %d" % (len(word_to_id), len(vocab))

test_input = "./test_input.txt"
#pred.predict_ngram(test_input, vocab)

#pred.predict_ngram(test_input, vocab)
pred.predict_ngram_before(test_input, vocab)
print "--------------------------RNN---------------------------------------"
#print "-----------------------------------------------------------------"
#pred.predict_rnn(test_input, vocab)

print "--------------------------RNN-BI---------------------------------------"

#pred.predict_bigram_rnn(test_input, vocab)