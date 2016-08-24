# ETH Program Analysis Project 2016 - Deep Learning for Programs

See Report cedlujs-deep-learning.pdf

## Requirements for running scripts:
* libraries installed (installDep.sh) -> SRILM must be downloaded by hand!
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

## How to create test/evaluation files in a directory of prepared js files:
*First Step:* mark desired method to predict with ``_<methodName>_`` in the js file 
Example:
```
var sys = require("sys");
var response = arg("func");

sys.puts("STATUS: " + response.statusCode);
sys.puts("HEADERS: " + JSON.stringify(response.headers));
response.setEncoding("UTF8");
response.addListener("data", function(chunk) {
  sys.puts("BODY: " + chunk);
});
response._addListener_();

```
*Second Step:* run the create_tests.py python script

```
python create_tests.py ./tests_nodejs/
```
Example output:
```
Process file ex01.js
Process file ex02.js
Process file ex03.js
Process file ex04.js
Process file ex05.js
Process file ex06.js
Process file ex07.js
Process file ex08.js
Process file ex09.js
Process file ex10.js
```
program.json, test pred_sol.txt should know be located in the defined directory.

## Code
* ``./ProgramAnalysis`` contains the java project for the pointer analysis and object history extraction.
* ``./languagemodels`` contains various python scripts for training and querying the language models.

## Used Libaries
* http://www.brics.dk/TAJS/
* http://www.speech.sri.com/projects/srilm/
* https://github.com/njsmith/pysrilm
* https://www.tensorflow.org/
* https://github.com/abort/javascript-call-graph
