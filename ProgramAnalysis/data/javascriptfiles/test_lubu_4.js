/** @type {number} */
var szmvar_c = 0;
/** @type {Array.<string>} */
var szmvar_cook = document.cookie.split(";");
/** @type {number} */
szmvar_i = 0;
for (;szmvar_i < szmvar_cook.length;szmvar_i++) {
    if (szmvar_cook[szmvar_i].match("POPUPCHECK=.*")) {
        /** @type {Date} */
        var szmvar_check = new Date;
        /** @type {number} */
        var szmvar_now = szmvar_check.getTime();
        szmvar_check.setTime(szmvar_cook[szmvar_i].split("=")[1]);
        if (szmvar_check.getTime() >= szmvar_now) {
            /** @type {number} */
            szmvar_c = 1;
        }
        break;
    }
}
if (szmvar_c == 0) {
    document.write('<script src="http://qs.wemfbox.ch/?' + szmvars + '"></' + "script>");
}
NET - Metrix;
