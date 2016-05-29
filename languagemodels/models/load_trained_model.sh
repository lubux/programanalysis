#!/bin/bash
SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
CUR_PATH=$(pwd)
cd $SCRIPTPATH

echo "Load Trained Models"
curl -L https://www.dropbox.com/s/9jj842q2um9odhe/model_big.zip?dl=1 > model_big.zip

unzip model_big.zip
mv ./model_big/* .
rm model_big.zip
rm -r ./model_big/

cd $CUR_PATH