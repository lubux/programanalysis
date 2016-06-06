var obj = new Object();
var pos = obj.offset();
pos.top += parseInt(obj.css("paddingTop"), 10) || 0;
pos.left += parseInt(obj.css("paddingLeft"), 10) || 0;
pos.top += parseInt(obj.css("border-top-width"), 10) || 0;
pos.left += parseInt(obj._css_(), 10) || 0;
pos.width = obj.width();
pos.height = obj.height();	