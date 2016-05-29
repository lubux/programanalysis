from codepred.Vocabulary import Vocabulary
import codepred.Vocabulary as pre

files = ["./data/trainingSet_part1.txt",
         "./data/trainingSet_part2.txt"]
MAX_VOCAB = 20000

prep = Vocabulary()
for loc in files:
    prep.process(loc)

prep.gen_data(MAX_VOCAB)

[_, word_to_id, vocab] = pre.load_vocab_data()
print "%d %d" % (len(word_to_id), len(vocab))

print "Vocab Size: %d Trimmed to %d" % (prep.get_vocab_size(), MAX_VOCAB)
print "Num Histories: %d" % (prep.num_hist,)

num_eval = prep.num_hist // 5
pre.create_train(files, prep.num_hist-num_eval, word_to_id)
