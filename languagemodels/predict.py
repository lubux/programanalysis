import argparse
import pred_funcs as pred

MODEL_NGRAM_FLAG = "ngram"
MODEL_RNN_FLAG = "rnn"


parser = argparse.ArgumentParser(description='Predict best sentence')
parser.add_argument("-m", "--model", help="the model to use (%s/%s)" % (MODEL_NGRAM_FLAG, MODEL_RNN_FLAG),
                    action="store", required=True)

parser.add_argument("-i", "--input", help="the path of the input file",
                    action="store", required=True)

args = parser.parse_args()

if args.model == MODEL_NGRAM_FLAG:
    pred.predict_ngram(args.input)
elif args.model == MODEL_RNN_FLAG:
    pred.predict_rnn(args.input)

