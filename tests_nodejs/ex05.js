function on_post(stream) {
  stream.setEncoding("utf8");
  stream.addListener("connect", function() {
    sys.puts("Client connected");
    stream.write("hello");
  });
  stream.addListener("data", function(data) {
    sys.puts("Received from client: " + data);
    stream.write(data);
  });
  stream._addListener_()
}

var stream = arg("Stream");
on_post(stream);
