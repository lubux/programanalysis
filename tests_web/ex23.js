var el = new Object();
/** @type {Array} */
var props = ["position", "top", "bottom", "left", "right"];
$.effects.setMode(el, o.options.mode || "effect");
var rvar = o.options.direction || "left";
var i = o.options.distance || 20;
var padLength = o.options.times || 3;
var cycle = o.duration || (o.options.duration || 140);
$.effects.save(el, props);
el.show();
$.effects.createWrapper(el);
/** @type {string} */
var ref = rvar == "up" || rvar == "down" ? "top" : "left";
/** @type {string} */
var motion = rvar == "up" || rvar == "left" ? "pos" : "neg";
rvar = {};
var optgroup = {};
var vvarText = {};
/** @type {string} */
rvar[ref] = (motion == "pos" ? "-=" : "+=") + i;
/** @type {string} */
optgroup[ref] = (motion == "pos" ? "+=" : "-=") + i * 2;
/** @type {string} */
vvarText[ref] = (motion == "pos" ? "-=" : "+=") + i * 2;
el.animate(rvar, cycle, o.options.easing);
/** @type {number} */
i = 1;
for (;i < padLength;i++) {
  el.animate(optgroup, cycle, o.options.easing).animate(vvarText, cycle, o.options.easing);
}
el.animate(optgroup, cycle, o.options.easing).animate(rvar, cycle / 2, o.options.easing, function() {
  $.effects.restore(el, props);
  $.effects.removeWrapper(el);
  if (o.callback) {
    o.callback.apply(this, arguments);
  }
});
el.queue("fx", function() {
  el.dequeue();
});
el._dequeue_();