target = document.id(target);
element = document.id(element);
var targets = this.togglers.contains(target);
this.togglers.include(target);
this.elements.include(element);
var item = this.togglers.indexOf(target);
var one = this.display.bind(this, item);
target.store("accordion:display", one);
target.addEvent(this.options.trigger, one);
if (this.options.height) {
  element.setStyles();
}
if (this.options.width) {
  element.setStyles();
}
/** @type {number} */
element.fullOpacity = 1;
if (this.options.fixedWidth) {
  element.fullWidth = this.options.fixedWidth;
}
if (this.options.fixedHeight) {
  element.fullHeight = this.options.fixedHeight;
}
element._setStyle_();