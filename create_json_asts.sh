#!/bin/bash
# Assumes js parser is locatet in the programanalysis folder(PATH_JS_PARSER)
if [ "$#" -ne 1 ]; then
    echo "Illegal number of arguments"
    echo "usage: <scriptname> <js_directory>"
    exit 1
fi

SCRIPT_PATH=$( cd $(dirname $0) ; pwd -P )

FILES=$1
PROGRAMS="programs.json"
PATH_JS_PARSER="$SCRIPT_PATH/js_parser/bin/js_parser.js"
NODE_JS="node"

if [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
	NODE_JS="nodejs"
fi

for f in $FILES/*.js
do
	echo "Processing $f"
	node $PATH_JS_PARSER $f >> $FILES/$PROGRAMS
done