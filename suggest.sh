#!/bin/bash

if [[ $# -ne 2 ]]; then
	echo "Error: Expected exactly 2 arguments"
	echo "$0 programs test"
	echo "    programs: file containing JavaScript AST in JSON format (programs.json)"	
	echo "    test    : file containing testing examples"
	echo ""
	echo "For example:"
	echo "$0 tests_suggest/programs.json tests_suggest/test"
	echo ""
	echo "Test file defines a set of testing examples (one on each line) in the following format:"
	echo "    <tree_id> <node_id>"
	echo "  where:"
	echo "    <tree_id> denotes index of the AST in the programs input file and,"
	echo "    <node_id> denotes the position in the tree to complete. These positions will correspond to the name of an API where the value of the node at this position will be set to '?'"
	echo ""
	echo ""
	echo "Output format: for each testing example you should output top 5 suggestions in the following format:"
	echo "    <tree_id> <node_id> <rank> <probability> <suggestion>"
	echo "  where:"
	echo "    <tree_id> and <node_id> denote AST tree and position in the tree for which the completion is computed"
	echo "    <rank> is an integer denoting rank of the completion (i.e., rank 1 corresponds to the most likely suggestion)"
	echo "    <probability> a floating point number in the range <0, 1> denoting the probability of the suggestion"
	echo "    <suggestion> the actual suggestion (i.e., name of the API)"
	echo ""
	echo "For example, given input following file:"
	echo "0 42"
	echo ""
	echo "The concrete output can be:"
	echo "0 42 1 0.3 length"
	echo "0 42 2 0.2 push"
	echo "0 42 3 0.1 pop"
	echo "0 42 4 0.04 clear"
	echo "0 42 5 0.03 empty"
	echo ""
	echo "You can also output less than 5 suggestions in case the system assigns zero probability to any other label."
	echo ""
	echo "All the output should be produced to stdout. Any debugging logs should go to stderr."
	exit -1
fi

SCRIPT_PATH=$( cd $(dirname $0) ; pwd -P )
CUR_PATH=$(pwd)

# Run your analysis and produce output as defined above
cd $SCRIPT_PATH/ProgramAnalysis/
java -jar ProgramAnalysis.jar -m test_predict -pf $1 -tf $2 > $SCRIPT_PATH/languagemodels/tmp_input.txt 2> /dev/null
cd $SCRIPT_PATH/languagemodels/
python predict.py -m rnn -i ./tmp_input.txt
rm tmp_input.txt
cd CUR_PATH
