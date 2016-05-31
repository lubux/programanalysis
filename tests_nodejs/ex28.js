var express = require('express');
var bodyParser = require('body-parser');
var _ = require('lodash');
var app = express();
var dictionary = JSON.parse(
  require('fs').readFileSync('./lib/dictionary.json')
).dictionary;

var words = require('./lib/words');
app.set('view engine', 'ejs');
app.set('view options', { layout: false });
app.use('/public', express.static('public'));

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.get('/', function (req, res) {
  res.render('index', { pattern: null });
});

app.post('/search', function (req, res) {
  var result = words.search(req.body.pattern, dictionary).result;
  res.render('result', { words: result, pattern: req.body.pattern });
});

app._listen_();
console.log("Listening on port: " + (process.env.PORT || 3000));