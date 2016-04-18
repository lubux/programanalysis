from srilm import LM
from NGRAMPredictions import NGramPredictor, BigramPredictor

def pred_words():
    pred = NGramPredictor("../temp/templm3")
    res = pred.get_best_fit(["name", "My"], 3)
    print map(lambda (a, b): (a, 10**b), res)

def pred_sentences():
    pred = NGramPredictor("../temp/templm3")
    res2 = pred.get_best_sentence([["My", "name", "is"], ["My", "name", "cat"], ["My", "name", "cancer"]], 3)
    print map(lambda (a, b): (a, 10**b), res2)

def pred_sentences():
    pres = BigramPredictor('test.db')
    pres.store_birams('../temp/templm2')
    res = pres.get_candidates_for_word('is')
    print res
    pres.close()