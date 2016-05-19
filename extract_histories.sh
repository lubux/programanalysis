#!/bin/bash

if [[ $# -ne 2 ]]; then
	echo "Error: Expected exactly 2 arguments"
	echo "$0 programs test"
	echo "    programs: file containing JavaScript AST in JSON format (programs.json)"	
	echo "    test    : file containing testing examples"
	echo ""
	echo "For example:"
	echo "$0 tests_histories/programs.json tests_histories/test"
	echo ""
	echo "Test file defines a set of testing examples (one on each line) in the following format:"
	echo "    <tree_id> <node_id>"
	echo "  where:"
	echo "    <tree_id> denotes index of the AST in the programs input file and,"
	echo "    <node_id> denotes the position for which to extract object histories (i.e., a node of type Identifier)"
	echo ""
	echo ""
	echo "Output format: for each testing example you should the object history in the following format:"
	echo "    <tree_id> <node_id> <token_1> ... <token_n>"
	echo "  where:"
	echo "    <tree_id> and <node_id> denote AST tree and position in the tree for which the history is computed"
	echo "    <token_1> ... <token_n> is the object history and its concrete form depens on the selected abstraction. These should be the tokens over which the probabilistic model is trained. Note that the tokens should not contain any whitespace characters as they are used as delimiters."
	echo ""
	echo ""
	echo "All the output should be produced to stdout. Any debugging logs should go to stderr."
	exit -1
fi


# Run your analysis and produce output as defined above
