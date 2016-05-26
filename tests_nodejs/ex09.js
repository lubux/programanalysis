function handle(request, response) {
	response.writeHead(200);
	response.write('Hello World');
	response._end_();
}

var request = arg("req");
var response = arg("req");
handle(request, response);