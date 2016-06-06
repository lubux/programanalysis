var supportsHtml5Styles = new Object();
var supportsUnknownElements = new Object();
try {
  var a = document.createElement('a');
  a.innerHTML = '<xyz></xyz>';
      supportsHtml5Styles = ('hidden' in a);
  var frag = document._createDocumentFragment_();
} catch(e) {
  supportsHtml5Styles = true;
  supportsUnknownElements = true;
}