var Hash = new Native({
  name : "Hash",
  /**
   * @param {Object} object
   * @return {?}
   */
  initialize : function(object) {
    if ($type(object) == "hash") {
      object = $unlink(object.getClean());
    }
    var key;
    for (key in object) {
      this[key] = object[key];
    }
    return this;
  }
});
Hash.implement({
  /**
   * @param {Function} fn
   * @param {?} thisv
   * @return {undefined}
   */
  forEach : function(fn, thisv) {
    var key;
    for (key in this) {
      if (this.hasOwnProperty(key)) {
        fn.call(thisv, this[key], key, this);
      }
    }
  },
  /**
   * @return {?}
   */
  getClean : function() {
    var old = {};
    var name;
    for (name in this) {
      if (this.hasOwnProperty(name)) {
        old[name] = this[name];
      }
    }
    return old;
  },
  /**
   * @return {?}
   */
  getLength : function() {
    /** @type {number} */
    var length = 0;
    var prop;
    for (prop in this) {
      if (this.hasOwnProperty(prop)) {
        length++;
      }
    }
    return length;
  }
});
Hash._alias_();