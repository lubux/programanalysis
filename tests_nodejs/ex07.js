function test(stream) { 
  if (stream._error) {
  	return;
  }
  if (!stream.isMultiPart) {
  	stream.emit("partEnd", stream);
  }
  stream._emit_();
}
var stream = new Object();
test(stream);
