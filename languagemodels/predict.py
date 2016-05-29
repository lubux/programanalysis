import argparse
import pred_funcs as pred
import codepred.Vocabulary as voc

MODEL_NGRAM_FLAG = "ngram"
MODEL_RNN_FLAG = "rnn"
MODEL_COMB_FLAG = "rnn_ngram"
MODEL_EVAL_FLAG = "all"


parser = argparse.ArgumentParser(description='Predict best sentence')
parser.add_argument("-m", "--model", help="the model to use (ngram, rnn, rnn_ngram, all)",
                    action="store", required=True)

parser.add_argument("-i", "--input", help="the path of the input file",
                    action="store", required=True)

args = parser.parse_args()

[max_sent_len, word_to_id, vocab] = voc.load_vocab_data()

if args.model == MODEL_NGRAM_FLAG:
    pred.predict_ngram_before(args.input, vocab)
elif args.model == MODEL_RNN_FLAG:
    pred.predict_rnn(args.input, vocab)
elif args.model == MODEL_COMB_FLAG:
    pred.combine_rnn_ngram_before(args.input, word_to_id)
elif args.model == MODEL_EVAL_FLAG:
    print MODEL_NGRAM_FLAG
    pred.predict_ngram_before(args.input, vocab)
    print MODEL_RNN_FLAG
    pred.predict_rnn(args.input, vocab)
    print MODEL_COMB_FLAG
    pred.combine_rnn_ngram_before(args.input, word_to_id)

