#!/bin/bash
SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
CUR_PATH=$(pwd)
cd $SCRIPTPATH
echo "Load Files:"
curl -L https://www.dropbox.com/sh/yx585ppchc58f83/AAD5dFNIz-B5yRjanuNr2soqa/trainingSet_part1.txt?dl=1 > trainingSet_part1.txt
echo "File 1 done"
curl -L https://www.dropbox.com/sh/yx585ppchc58f83/AABldiTcbWET71k1Dl2Du21Ta/trainingSet_part2.txt?dl=1 > trainingSet_part2.txt
echo "File 2 done"


echo "Create dataset" 
cd ..
python2.7 create_data_files.py

echo "Remove unused files"
rm ./data/trainingSet_part1.txt
rm ./data/trainingSet_part2.txt

cd $CUR_PATH