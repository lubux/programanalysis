from codepred.Vocabulary import Vocabulary
import codepred.Vocabulary as pre
import pred_funcs as pred
from codepred.NGRAMPredictor import BigramPredictor

"""
data_file = "./data/trainingSet.txt"

prep = Vocabulary()
prep.process(data_file)

num_eval = prep.num_hist // 4
pre.create_train(data_file, prep.num_hist-num_eval, prep.vocab_to_count)
"""
bigram = BigramPredictor("./models/ngram_lm2db")
bigram.store_birams("./models/ngram_lm2")


test_input = "./test_input.txt"
pred.predict_ngram(test_input)

