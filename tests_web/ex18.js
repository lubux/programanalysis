var doc = new Object();
var braceStack = uHostName.split("|");
doc = doc.createDocumentFragment();
if (doc.createElement) {
  for (;braceStack.length;) {
    doc.createElement(braceStack.pop());
  }
}
doc._createElement_();