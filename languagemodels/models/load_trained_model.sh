#!/bin/bash
SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
CUR_PATH=$(pwd)
cd $SCRIPTPATH

echo "Load Trained Models"
curl -L https://www.dropbox.com/s/wmcudw7fc8xv14n/model_vb.zip?dl=1 > model_vb.zip

unzip model_vb.zip
mv ./model_vb/* .
rm model_vb.zip
rm -r ./model_vb/

cd $CUR_PATH