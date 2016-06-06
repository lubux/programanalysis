var temp = new Object();
var obj = new Object();
var keys = [];
var key;
for (key in obj) {
  if (temp.isFunction(obj[key])) {
    keys.push(key);
  }
}
keys._sort_();