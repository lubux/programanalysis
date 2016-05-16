import re
from codepred.NGRAMPredictor import BigramPredictor
from codepred.NGRAMPredictor import NGramPredictor

MODEL_NGRAM_FLAG = "ngram"
MODEL_RNN_FLAG = "rnn"
CAND_TOKEN = "[?]"
LM_BIGRAM = "ngram_lm2db"
LN_NGRAM = "ngram_lm3"
REG_PAT = re.compile(r'(\d+)[ \t](\d+)')


def read_input(input):
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
                cur_hist.append(line)
        if cur_node is not None:
            res.append((cur_node, cur_hist))
    return res

def get_cadidates(bigram, hists):
    candidates = set()
    res = {}
    for step, hist in enumerate(hists):
        tokens = hist.split()
        searched = tokens.index(CAND_TOKEN)-1
        cur_cands = set(bigram.get_candidates_for_word(tokens[searched]))
        if step==0:
            candidates.update(cur_cands)
        else:
            candidates.intersection_update(cur_cands)
        res[hist] = cur_cands
    temp = []
    for (a, b) in res.items():
        b.intersection_update(candidates)
        temp.append((a, b))
    return candidates, dict(temp)


def predict_ngram(input):
    work_list = read_input(input)
    bigram = BigramPredictor("./models/%s" % (LM_BIGRAM,))
    nrgam_model = NGramPredictor("./models/%s" % (LN_NGRAM,))
    for work in work_list:
        node = work[0]
        hists = work[1]
        candidates, hist_to_cand = get_cadidates(bigram, hists)
        cands_to_props = {}
        for hist in hists:
            hist_cands = hist_to_cand[hist]
            sent_to_candidate = dict([(hist.replace(CAND_TOKEN, hist_cand), hist_cand) for hist_cand in hist_cands])
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
        print "%d %d" % (node[0], node[1])
        rank = 1
        for cur in ranking:
            print "%d %d %d %f %s" % (node[0], node[1], rank, cur[1], cur[0])
            rank += 1

def predict_before(input):
    work_list = read_input(input)
    bigram = BigramPredictor("./models/%s" % (LM_BIGRAM,))
    nrgam_model = NGramPredictor("./models/%s" % (LN_NGRAM,))
    for work in work_list:
        node = work[0]
        hists = work[1]
        candidates, hist_to_cand = get_cadidates(bigram, hists)
        cands_to_props = {}
        for hist in hists:
            hist_cands = hist_to_cand[hist]
            sent_to_candidate = dict([(hist.replace(CAND_TOKEN, hist_cand), hist_cand) for hist_cand in hist_cands])
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
        print "%d %d" % (node[0], node[1])
        rank = 1
        for cur in ranking:
            print "%d %d %d %f %s" % (node[0], node[1], rank, cur[1], cur[0])
            rank += 1


def predict_rnn(input):
    return None