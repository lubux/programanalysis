import re
from codepred.NGRAMPredictor import BigramPredictor
from codepred.NGRAMPredictor import NGramPredictor
from codepred.RNNPredictor import LSTMPredictor
import codepred.Vocabulary as voc
import numpy as np

MODEL_NGRAM_FLAG = "ngram"
MODEL_RNN_FLAG = "rnn"
CAND_TOKEN = "<?>"
LM_BIGRAM = "ngram_lm2db"
LN_NGRAM = "ngram_lm3"

REG_PAT = re.compile(r'(\d+)[ \t](\d+)')
TOKEN_PATTERN = re.compile(r'\[(.*)\]')

NOT_ALLOWED_TOKEN = [voc.TOKEN_START, voc.TOKEN_END, voc.TOKEN_UNKOWN]
NUM_CAND = 5


def process_hist_line(line, vocab):
    tokens = line.split()
    res = [voc.TOKEN_START]
    for token in tokens:
        if token in vocab or token in CAND_TOKEN:
            res.append(token)
        else:
            res.append(voc.TOKEN_UNKOWN)
    res.append(voc.TOKEN_END)
    return res


def read_input(input, vocab):
    cur_node = None
    cur_hist = []
    res = []
    with open(input, "r") as in_f:
        for line in in_f:
            match = REG_PAT.match(line)
            if match:
                if cur_node is not None:
                    res.append((cur_node, cur_hist))
                    cur_hist = []
                cur_node = (int(match.group(1)), int(match.group(2)))
            else:
                if cur_node is None:
                    continue
                cur_hist.append(process_hist_line(line, vocab))
        if cur_node is not None:
            res.append((cur_node, cur_hist))
    return res


def get_candidates(bigram, hists):
    candidates = set()
    res = {}
    for step, hist in enumerate(hists):
        searched = hist.index(CAND_TOKEN)-1
        cur_cands = set(bigram.get_candidates_for_word(hist[searched]))
        if step==0:
            candidates.update(cur_cands)
        else:
            candidates.intersection_update(cur_cands)
        res[hist_to_str(hist)] = cur_cands
    temp = []
    for (a, b) in res.items():
        b.intersection_update(candidates)
        temp.append((a, b))
    return candidates, dict(temp)


def detokenize(token):
    match = TOKEN_PATTERN.match(token)
    if match:
        return match.group(1)
    else:
        raise RuntimeError("Inavlid token " + token)


def hist_to_str(hist):
    return " ".join(hist)


def print_ranking(node, ranking):
    print "%d %d" % (node[0], node[1])
    rank = 1
    for cur in ranking:
        if rank > NUM_CAND or cur[0] in NOT_ALLOWED_TOKEN:
            continue
        print "%d %d %d %f %s" % (node[0], node[1], rank, cur[1], detokenize(cur[0]))
        rank += 1
    print ""


# old sentence prob evil
def predict_ngram(input, vocab):
    work_list = read_input(input, vocab)
    bigram = BigramPredictor("./models/%s" % (LM_BIGRAM,))
    nrgam_model = NGramPredictor("./models/%s" % (LN_NGRAM,))
    try:
        for work in work_list:
            node = work[0]
            hists = work[1]
            candidates, hist_to_cand = get_candidates(bigram, hists)
            cands_to_props = {}
            for hist in hists:
                hist_cands = hist_to_cand[hist_to_str(hist)]
                sent_to_candidate = dict([(hist_to_str(hist).replace(CAND_TOKEN, hist_cand), hist_cand)
                                          for hist_cand in hist_cands])
                sent_to_preds = dict(nrgam_model.sentence_score_prediction(sent_to_candidate.keys()))
                for sent in sent_to_candidate.keys():
                    word = sent_to_candidate[sent]
                    if word in cands_to_props:
                        list = cands_to_props[word]
                        list.append(sent_to_preds[sent])
                        cands_to_props[word] = list
                    else:
                        cands_to_props[word] = [sent_to_preds[sent]]
            ranking = sorted([(a, sum(b)) for (a, b) in cands_to_props.items()], key=lambda x:x[1], reverse=True)
            print_ranking(node, ranking)
    finally:
        bigram.close()


def predict_ngram_before(input, vocab):
    work_list = read_input(input, vocab)
    bigram = BigramPredictor("./models/%s" % (LM_BIGRAM,))
    nrgam_model = NGramPredictor("./models/%s" % (LN_NGRAM,))
    try:
        for work in work_list:
            node = work[0]
            hists = work[1]
            candidates, hist_to_cand = get_candidates(bigram, hists)
            cands_to_props = {}
            for hist in hists:
                hist_str = hist_to_str(hist)
                hist_cands = list(hist_to_cand[hist_str])
                context = hist[:hist.index(CAND_TOKEN)]
                props = nrgam_model.get_context_prop(context, hist_cands)
                for ind, cand in enumerate(hist_cands):
                    if cand in cands_to_props:
                        list_in = cands_to_props[cand]
                        list_in.append(props[ind])
                        cands_to_props[cand] = list_in
                    else:
                        cands_to_props[cand] = [props[ind]]
            ranking = sorted([(a, np.mean(np.asarray(b)))
                              for (a, b) in cands_to_props.items()], key=lambda x:x[1], reverse=True)
            print_ranking(node, ranking)
    finally:
        bigram.close()


def combine_rnn_ngram_before(input, word_to_id, save_dir="./models/"):
    work_list = read_input(input, word_to_id)
    bigram = BigramPredictor("./models/%s" % (LM_BIGRAM,))
    nrgam_model = NGramPredictor("./models/%s" % (LN_NGRAM,))
    rnn = LSTMPredictor(save_dir)
    try:
        for work in work_list:
            node = work[0]
            hists = work[1]
            contexts = [hist[:hist.index(CAND_TOKEN)] for hist in hists]
            scores = rnn.score_complentions(contexts)
            candidates, hist_to_cand = get_candidates(bigram, hists)
            cands_to_props = {}
            for hist in hists:
                hist_str = hist_to_str(hist)
                hist_cands = list(hist_to_cand[hist_str])
                context = hist[:hist.index(CAND_TOKEN)]
                props = nrgam_model.get_context_prop(context, hist_cands)
                for ind, cand in enumerate(hist_cands):
                    if cand in cands_to_props:
                        list_in = cands_to_props[cand]
                        list_in.append(props[ind])
                        cands_to_props[cand] = list_in
                    else:
                        cands_to_props[cand] = [props[ind]]
            preds_ngram = [(a, np.mean(np.asarray(b))) for (a, b) in cands_to_props.items()]
            final_score = [(a, (b + scores[word_to_id[a]][1]) / 2) if a in word_to_id else (a, b) for (a, b) in preds_ngram]
            if len(final_score) < len(NOT_ALLOWED_TOKEN) + NUM_CAND:
                in_ranking = [a for (a, b) in final_score if a not in NOT_ALLOWED_TOKEN]
                if len(in_ranking) < NUM_CAND:
                    scores.sort(key=lambda x:x[1], reverse=True)
                    count = len(in_ranking)
                    for score in scores:
                        if score[0] not in in_ranking and score[0] not in NOT_ALLOWED_TOKEN:
                            final_score.append(score)
                            count += 1
                        if count >= NUM_CAND:
                            break
            final_score.sort(key=lambda x:x[1], reverse=True)
            print_ranking(node, final_score)
    finally:
        rnn.close()
        bigram.close()


def predict_rnn(input, vocab, save_dir="./models/"):
    work_list = read_input(input, vocab)
    rnn = LSTMPredictor(save_dir)
    try:
        for work in work_list:
            node = work[0]
            hists = work[1]
            contexts = [hist[:hist.index(CAND_TOKEN)] for hist in hists]
            scores = rnn.score_complentions(contexts)
            ranking = sorted(scores, key=lambda t: t[1], reverse=True)
            print_ranking(node, ranking)
    finally:
        rnn.close()


def predict_bigram_rnn(input, vocab, save_dir="./models/"):
    work_list = read_input(input, vocab)
    rnn = LSTMPredictor(save_dir)
    bigram = BigramPredictor("./models/%s" % (LM_BIGRAM,))
    try:
        for work in work_list:
            node = work[0]
            hists = work[1]
            candidates, hist_to_cand = get_candidates(bigram, hists)
            cands_to_props = {}
            for hist in hists:
                hist_str = hist_to_str(hist)
                hist_cands = list(hist_to_cand[hist_str])
                context = hist[:hist.index(CAND_TOKEN)]
                props = rnn.get_context_prop(context, hist_cands)
                for ind, cand in enumerate(hist_cands):
                    if cand in cands_to_props:
                        list_in = cands_to_props[cand]
                        list_in.append(props[ind])
                        cands_to_props[cand] = list_in
                    else:
                        cands_to_props[cand] = [props[ind]]
            ranking = sorted([(a, np.mean(np.asarray(b)))
                              for (a, b) in cands_to_props.items()], key=lambda x: x[1], reverse=True)
            print_ranking(node, ranking)
    finally:
        rnn.close()
