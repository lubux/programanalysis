# ETH Program Analysis Project 2016 - Deep Learning for Programs

## Requirements for running scripts:
* libraries installed (installDep.sh)
* Download the pretrainend models (./languagemodels/models/load_trained_model.sh) 

## How to run the evaluation script:
```
./eval_suggest.sh ./tests_nodejs/programs.json ./tests_nodejs/test ./tests_nodejs/pred_sol.txt
```

Example output:
```
Evaluated model ngram
Top 1: Percentage 40.00%
Top 2: Percentage 60.00%
Top 3: Percentage 70.00%
Top 4: Percentage 70.00%
Top 5: Percentage 70.00%
Evaluated model rnn
Top 1: Percentage 40.00%
Top 2: Percentage 60.00%
Top 3: Percentage 70.00%
Top 4: Percentage 70.00%
Top 5: Percentage 70.00%
```

## How to run suggest script:
```
./suggest.sh ./tests_nodejs/programs.json ./tests_nodejs/test
```

## How to run hist_suggest script:
```
./extract_histories.sh ./tests_histories/programs.json ./tests_histories/test
```