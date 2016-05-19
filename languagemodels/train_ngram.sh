#!/bin/bash

while [[ $# > 1 ]]
do
key="$1"

case $key in
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
ngram-count -text $TRAINPATH -order 2 -lm ./models/ngram_lm2 -gt3min 1 -gt4min 1 -kndiscount -interpolate -unk
ngram-count -text $TRAINPATH -order 3 -lm ./models/ngram_lm3 -gt3min 1 -gt4min 1 -kndiscount -interpolate -unk

ngram -ppl ./data/val.txt -lm ./models/ngram_lm3 -unk >> ./models/ngram_lm3_perplexity.txt