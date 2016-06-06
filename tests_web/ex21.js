jQuery.support = {};
/** @type {Element} */
var el = doc.documentElement;
/** @type {Element} */
var script = doc.createElement("script");
/** @type {Element} */
var div = doc.createElement("div");
var id = "script" + jQuery.now();
/** @type {string} */
div.style.display = "none";
/** @type {string} */
div.innerHTML = "   <link/><table></table><a href='/a' style='color:red;float:left;opacity:.55;'>a</a><input type='checkbox'/>";
/** @type {NodeList} */
var all = div.getElementsByTagName("*");
var a = div._getElementsByTagName_();