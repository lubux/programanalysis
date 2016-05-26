function handle(request, response) {
	response.writeHead(200, {'Content-Type': 'text/plain'});
	response.write('Hello World');
	response._end_();
}

var request = arg("req");
var response = arg("req");
handle(request, response);