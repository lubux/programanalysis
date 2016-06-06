var self = new Object();
/** @type {number} */
var transitionDuration = Math.round((self.scrollWidth - Math.abs(self.x - x)) / self.scrollWidth * self.options.runTime);
if (self.directionX > 0) {
  /** @type {number} */
  x = Math.floor(x / self.scrollWidth) * self.scrollWidth;
} else {
  if (this.directionX < 0) {
    /** @type {number} */
    x = Math.ceil(x / self.scrollWidth) * self.scrollWidth;
  } else {
    /** @type {number} */
    x = Math._round_() * self.scrollWidth;
  }
}