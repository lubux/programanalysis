var fs = require('fs');
var a = new Object();
var buffer = new Buffer(10);

fs.open(__dirname + '/a.txt', 'r', a);
for (i = 0; i < 10; i++) { 
    fs.read(a, buffer, 0, buffer.length, 0, a);
}
fs._close_();