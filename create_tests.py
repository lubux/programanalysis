import os
import sys
import platform
import re
import subprocess

MY_PATH = os.path.split(os.path.abspath(__file__))
PATH_JS_PARSER = os.path.join(MY_PATH[0], "js_parser/bin/js_parser.js")
NODE_JS = "node"
PROGRAMS = "programs.json"
TEST_F = "test"
SOL_F = "pred_sol.txt"
pat = re.compile(r'.*"id":(\d+), "type":".*", "value":"_(.*)_".*')
DO_SUGGEST = True

if len(sys.argv) < 2:
    print("Invalid number of arguments")
    print("usage: python <scriptname> <js_directory> optional:--hist")
    exit(1)

if len(sys.argv) == 3:
    if sys.argv[2] == "--hist":
        DO_SUGGEST = False

if not DO_SUGGEST:
    PROGRAMS = "hist_"+PROGRAMS
    TEST_F = "hist_"+TEST_F
    SOL_F = "hist_"+SOL_F

path = sys.argv[1]


if "Linux" in platform.platform():
    NODE_JS = "nodejs"


def run_command(cmd, arg1, arg2):
    output, _ = subprocess.Popen([cmd, arg1, arg2], stdout=subprocess.PIPE).communicate()
    return output.decode("utf-8") 

with open(os.path.join(path, PROGRAMS), "w") as progs_w, \
        open(os.path.join(path, TEST_F), "w") as test_w, \
        open(os.path.join(path, SOL_F), "w") as sol_w:
    program_id = 0
    for js_file in os.listdir(path):
        if js_file.endswith(".js"):
            print("Process file %s" % (js_file,))
            json_ast = run_command(NODE_JS, PATH_JS_PARSER, os.path.join(path, js_file))
            tokens = json_ast.split("}, {")
            found = False
            for token in tokens:
                match = pat.match(token)
                if match:
                    node_id = match.group(1)
                    name = match.group(2)
                    found = True
                    if DO_SUGGEST:
                        json_ast = json_ast.replace("_"+name+"_", "?")
                    else:
                        json_ast = json_ast.replace("_"+name+"_", name)
                    progs_w.write(json_ast)
                    test_w.write("%d    %s\n" % (program_id, node_id))
                    if DO_SUGGEST:
                        sol_w.write("%d %s %s\n" % (program_id, node_id, name))
                    found = True
                    break
            if not found:
                print("Program wit id %d has no method marked :( -> ignore" % (program_id-1,))
            program_id += 1