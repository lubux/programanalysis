var http = require('http'),
    port = 8000;

var server = http.createServer(function (request, response) {
  response.writeHead(200, {"Content-Type": "text/plain"});
  response.end("Hello World\n");
});

server._listen_();
console.log("Listening on <insert your favorite ip>:" + port);