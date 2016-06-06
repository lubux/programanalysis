var $e = new Object();
var set = $(".voting-value-average", $e);
/** @type {number} */
var value = 0;
/** @type {number} */
var fontSize = 0;
$e.show();
$e._find_().each(attachLightbox);
var index;
for (index in iteratee) {
  value = iteratee[index];
  /** @type {number} */
  fontSize = 96 + (parseFloat(value) - 1) * 31;
  set.eq(index).css("left", fontSize).text(parseFloat(value).toFixed(1));
}
/** @type {number} */
var i = 0;
for (;i < a.length;i++) {
  set.eq(a[i]).css("left", "55%").text("--");
}