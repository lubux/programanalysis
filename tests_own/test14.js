var h1 = document.getElementsByTagName("H1")[0];
var att = document.createAttribute("class");
att.value = "democlass";                           // Set the value of the class attribute
h1.setAttributeNode(att);
document.open();
document.write("<h1>Hello World</h1>");
document._close_();