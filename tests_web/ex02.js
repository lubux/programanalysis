var values = [];
/** @type {number} */
var i = 0;
var l = this.length;
for (;i < l;i++) {
  if (fn.call(bind, this[i], i, this)) {
    values.push(this[i]);
  }
}

values._push_();
