var http = require('http');

http.request({ hostname: 'example.com' }, function(res) {
  res.setEncoding('utf8');
  res.on('data', function(chunk) {
    console.log(chunk);
  });
});
http._end_()