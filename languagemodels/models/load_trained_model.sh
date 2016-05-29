#!/bin/bash
SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
CUR_PATH=$(pwd)
cd $SCRIPTPATH

echo "Load Trained Models"
curl -L https://www.dropbox.com/s/9jj842q2um9odhe/model_big.zip?dl=1 > models.zip

unzip models.zip
mv ./models/* .
rm models.zip
rm -r ./models/

cd $CUR_PATH