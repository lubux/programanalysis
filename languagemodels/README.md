# Language Model part of the Project

## Implements
* n-gram model with SRILM and [pysrilm](https://github.com/njsmith/pysrilm)
* lstm-rnn model with [tensorflow](https://www.tensorflow.org/), based on the provided [tutorial](https://www.tensorflow.org/versions/r0.9/tutorials/recurrent/index.html#recurrent-neural-networks)
* an API for querying the model on history complention (predict.py)

## Code
* ``./models`` contains the trained models and a script for downloading them from dropbox
* ``./codepred`` contains the code, which implements n-gram and the lstm-rnn model.
* ``./data`` contains the history trainig/validation data and a script for downloading them from dropbox
* ``predict.py`` implements the api for computing the completions on the provided histories
* ``train_ngram.sh`` a script for training the n-gram model
* ``train_rnn.py`` a python script for training the lstm-rnn model
* ``train_rnn_aws.sh`` a script for training the lstm-rnn model on Amazon Web Services


