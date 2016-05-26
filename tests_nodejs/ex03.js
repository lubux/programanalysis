var sys = require("sys");
var response = arg("func");

sys.puts("STATUS: " + response.statusCode);
sys.puts("HEADERS: " + JSON.stringify(response.headers));
response.setEncoding("UTF8");
response.addListener("data", function(chunk) {
  sys.puts("BODY: " + chunk);
});
response._addListener_();
