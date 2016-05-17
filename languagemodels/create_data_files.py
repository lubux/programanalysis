from codepred.Vocabulary import Vocabulary
import codepred.Vocabulary as pre


data_file1 = "./data/trainingSet_part1.txt"
data_file2 = "./data/trainingSet_part2.txt"
MAX_VOCAB = 10000

prep = Vocabulary()
prep.process(data_file1)
prep.process(data_file2)
prep.gen_data(MAX_VOCAB)

print "Vocab Size: %d Trimmed to %d" % (prep.get_vocab_size(), MAX_VOCAB)
print "Num Histories: %d" % (prep.num_hist)

num_eval = prep.num_hist // 5
pre.create_train([data_file1, data_file2], prep.num_hist-num_eval, prep.vocab_to_count)
