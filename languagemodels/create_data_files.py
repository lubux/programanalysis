from codepred.Vocabulary import Vocabulary
import codepred.Vocabulary as pre
import pred_funcs as pred
from codepred.NGRAMPredictor import BigramPredictor


data_file = "./data/trainingSet_part1.txt"

prep = Vocabulary()
prep.process(data_file)
prep.gen_data(10000)

num_eval = prep.num_hist // 5
pre.create_train(data_file, prep.num_hist-num_eval, prep.vocab_to_count)