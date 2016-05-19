#!/bin/bash

AWS_ADDR=ubuntu@ec2-52-59-255-195.eu-central-1.compute.amazonaws.com
KEY_PATH=./aws/MLKey.pem

SSH_CMD="sudo ssh -i $KEY_PATH $AWS_ADDR" 
SCP_CMD="sudo scp -i $KEY_PATH "

echo "create dirs on AWS"
$SSH_CMD << EOF
mkdir training
cd training
mkdir codepred
mkdir data
mkdir models
cd ..
EOF

echo "upload data"
$SCP_CMD ./codepred/RNNPredictor.py $AWS_ADDR:./training/codepred/
$SCP_CMD ./codepred/Vocabulary.py $AWS_ADDR:./training/codepred/
$SCP_CMD ./codepred/__init__.py $AWS_ADDR:./training/codepred/
$SCP_CMD ./data/train.txt $AWS_ADDR:./training/data/
$SCP_CMD ./data/val.txt $AWS_ADDR:./training/data/
$SCP_CMD ./train_rnn.py $AWS_ADDR:./training/
$SCP_CMD ./models/vocab.p $AWS_ADDR:./training/models/

#echo "run training"
#$SSH_CMD << EOF
export CUDA_HOME=/usr/local/cuda
export CUDA_ROOT=/usr/local/cuda
export PATH=$PATH:$CUDA_ROOT/bin
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$CUDA_ROOT/lib64
cd training
screen python ./train_rnn.py | tee log.txt
EOF

read -p "Press to download data..."

echo "download data"
$SSH_CMD << EOF
cd training
mv log.txt ./models/
zip -r model_remote.zip ./models/*
EOF

$SCP_CMD $AWS_ADDR:./training/model_remote.zip .
unzip model_remote.zip  -d ./models/
rm model_remote.zip

echo "remove data"
$SSH_CMD << EOF
sudo rm -r training
EOF