function test(stream) {
    if (stream._error) {
        return;
    }
    if (!stream.isMultiPart) {
        stream.emit("partEnd", stream);
    }
    stream.PADeepLearningUniquePredFunc();
}
var stream = new Object();
test(stream);