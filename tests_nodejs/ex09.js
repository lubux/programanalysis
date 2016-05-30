function handle(request, response) {
	response.writeHead(200);
	response.write('Hello World');
	response._end_();
}

var request = new Object();
var response = new Object();
handle(request, response);