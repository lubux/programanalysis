function ender (stream) { 
  if (stream._error) {
  	return;
  }
  if (!stream.isMultiPart) {
  	stream.emit("partEnd", stream);
  }
  stream._emit_();
}
var stream = arg("Stream");
ender(sream);
