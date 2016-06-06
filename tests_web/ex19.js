  var expires = new Date;
  expires.setTime(expires.getTime() + 864E5 * opt_attributes);
  /** @type {string} */
  document.cookie = key + "=" + encodeURI(val) + "; path = /; expires=" + expires._toUTCString_();