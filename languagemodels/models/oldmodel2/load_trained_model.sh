#!/bin/bash
SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
CUR_PATH=$(pwd)
cd $SCRIPTPATH

echo "Load Trained Models"
curl -L https://www.dropbox.com/sh/wtf12r9upwjw0o1/AABFpr5vm6t2XKwxb3hlXJvfa?dl=1 > models.zip

unzip models.zip
mv ./models/* .
rm models.zip
rm -r ./models/

cd $CUR_PATH