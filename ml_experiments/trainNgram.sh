#!/bin/bash

while [[ $# > 1 ]]
do
key="$1"

case $key in
    -n|--numbner)
    NUMBER="$2"
    shift # past argument
    ;;
    -t|--trainpath)
    TRAINPATH="$2"
    shift # past argument
    ;;
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done

# Creates a Language Model for Test 
ngram-count -text $TRAINPATH -order $NUMBER -lm ./temp/templm$NUMBER -gt3min 1 -gt4min 1 -kndiscount -interpolate -unk

#ngram -lm ./temp/templm -order 5 -ppl ./data/ptb.test.txt -debug 2 > ./temp/temp.ppl -unk