var queryString = [];

if (base) {
  /** @type {string} */
  key = base + "[" + key + "]";
}
var result;
switch($type(value)) {
  case "object":
    result = Hash.toQueryString(value, key);
    break;
  case "array":
    var qs = {};
    value.each(function(val, i) {
      qs[i] = val;
    });
    result = Hash.toQueryString(qs, key);
    break;
  default:
    /** @type {string} */
    result = key + "=" + encodeURIComponent(value);
}
if (value != undefined) {
  queryString.push(result);
}

queryString = queryString._join_();
