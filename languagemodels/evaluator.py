import argparse
import re
import numpy as np

parser = argparse.ArgumentParser(description='Predict best sentence')
parser.add_argument("-sol", "--solution", help="the solution file",
                    action="store", required=True)

parser.add_argument("-res", "--result", help="the resulted file",
                    action="store", required=True)

args = parser.parse_args()

node = re.compile(r'(\d+) (\d+)')
result = re.compile(r'(\d+) (\d+) (\d+) (0.\d+) (.*)')
sol_pat= re.compile(r'(\d+) (\d+) (.*)')

NUM_BEST = 5
model_to_res = {}
with open(args.result, "r") as res:
    cur_model = None
    cur_node = None
    for line in res:
        m_node = node.match(line)
        m_result = result.match(line)
        if m_node and m_result:
            # result
            dict = model_to_res[cur_model]
            ranking = dict[cur_node]
            ranking.append(m_result.group(5))
        elif m_node:
            # node
            dict = model_to_res[cur_model]
            cur_node = (int(m_node.group(1)), int(m_node.group(2)))
            dict[cur_node] = []
        elif not line:
            continue
        elif len(line.split()) == 1:
            cur_model = line.strip()
            model_to_res[cur_model] = {}

sols = []
with open(args.solution, "r") as sol:
    for line in sol:
        m_sol = sol_pat.match(line)
        if m_sol:
            node = (int(m_sol.group(1)), int(m_sol.group(2)))
            sols.append((node, m_sol.group(3)))


for model in model_to_res.keys():
    data = model_to_res[model]
    counts = np.zeros(NUM_BEST)
    for sol in sols:
        ranks = data[sol[0]]
        if sol[1] in ranks:
            if ranks.index(sol[1]) < NUM_BEST:
                counts[ranks.index(sol[1]):] += 1
    percentages = (counts / len(sols)) * 100
    print "Evaluated model %s" % (model,)
    for step, per in enumerate(np.nditer(percentages)):
        print "Top %d: Percentage %.2f%%" % (step+1, per)




