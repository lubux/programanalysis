var styleObj= document.styleSheets[0].cssRules[0].style;
alert(styleObj.cssText);
for (var i = styleObj.length-1; i >= 0; i--) {
    var nameString = styleObj[i];
    styleObj.removeProperty(nameString);
}
alert(styleObj.cssText);
styleObj.setProperty("newProp", 5, 0);
styleObj._getPropertyValue_();
