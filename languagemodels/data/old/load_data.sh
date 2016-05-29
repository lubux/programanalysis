#!/bin/bash
SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
cd $SCRIPTPATH
echo "Load Files:"
curl -L https://www.dropbox.com/sh/yx585ppchc58f83/AAD5dFNIz-B5yRjanuNr2soqa/trainingSet_part1.txt?dl=1 > trainingSet_part1.txt
echo "File 1 done"
curl -L https://www.dropbox.com/sh/yx585ppchc58f83/AABldiTcbWET71k1Dl2Du21Ta/trainingSet_part2.txt?dl=1 > trainingSet_part2.txt
echo "File 2 done"
curl -L https://www.dropbox.com/sh/yx585ppchc58f83/AADgLvGEqK-1No6_nfB7bHo2a/trainingSet_part3.txt?dl=1 > trainingSet_part3.txt
echo "File 3 done"
curl -L https://www.dropbox.com/sh/yx585ppchc58f83/AACL4ERXGfslg-gfWTF0tsg6a/trainingSet_part4.txt?dl=1 > trainingSet_part4.txt
echo "File 4 done"
curl -L https://www.dropbox.com/sh/yx585ppchc58f83/AACiozoxnABM6KNNMZ0DNl-la/trainingSet_part5.txt?dl=1 > trainingSet_part5.txt
echo "File 5 done"


echo "Create dataset" 
cd ..
python2.7 create_data_files.py

echo "Remove unused files"
rm ./data/trainingSet_part1.txt
rm ./data/trainingSet_part2.txt
rm ./data/trainingSet_part3.txt
rm ./data/trainingSet_part4.txt
rm ./data/trainingSet_part5.txt