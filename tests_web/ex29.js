var days = 23;

var urlConfigHtml = "";
if (days !== undefined) {
  /** @type {Date} */
  var expires = new Date;
  if (days.toString().indexOf("m") != -1) {
    days.toString().replace("m", "");
    expires.setTime(expires.getTime() + days * 60 * 1E3);
    /** @type {string} */
    urlConfigHtml = "; expires=" + expires.toUTCString();
  } else {
    expires.setDate(expires.getDate() + days);
    /** @type {string} */
    urlConfigHtml = "; expires=" + expires._toGMTString_();
  }
}