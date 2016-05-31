function search(word, dictionary) {
  var exclude = filter(word, function(w) {
    return w != "?" && w != "_";
  });

  var excludePattern = "",
    toSearch = "";

  each(exclude, function(c) {
    excludePattern += "^" + c;
  });

  excludePattern = "[" + excludePattern + "]";

  toSearch = "^" + word.replace(/\?/g, excludePattern) + "$";

  toSearch = "^" + toSearch.replace(/\_/g, "[a-z]") + "$";

  if(word.indexOf("/") >= 0) {
    toSearch = word._replace_();
  }

  var result = new Object();

  return result;
}

var word = new Object();
var dictionary = new Object();
search(word, dictionary);