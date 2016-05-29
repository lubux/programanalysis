#!/bin/bash

if [[ $# -ne 3 ]]; then
	exit -1
fi

SCRIPT_PATH=$( cd $(dirname $0) ; pwd -P )
CUR_PATH=$(pwd)

# Run your analysis and produce output as defined above
cd $SCRIPT_PATH/ProgramAnalysis/
java -jar ProgramAnalysis.jar -m test_predict -pf $CUR_PATH/$1 -tf $CUR_PATH/$2 > $SCRIPT_PATH/languagemodels/tmp_input.txt 2> /dev/null
cd $SCRIPT_PATH/languagemodels/
python predict.py -m all -i ./tmp_input.txt > sol.data
python evaluator.py -sol $CUR_PATH/$3 -res ./sol.data
rm sol.data
rm tmp_input.txt
cd $CUR_PATH
