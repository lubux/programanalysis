function show_index(request, response) {
  sys.puts("Serving index page");
  response.writeHead(200);
  var output = '<html><head><title>node.js HTTP server example</title></head><body><b>Index output</b>';
  var url_request = url.parse(request.url).query;
  output += "<p>Request query: " + url_request + "</p>";
  response.write(output);
  response._end_();
}
var request = new Object();
var response = new Object();


show_index(request, response);