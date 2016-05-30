var fs = require('fs');
var a = new Object();
var buffer = new Buffer(10);

fs.open(__dirname + '/a.txt', 'r', a);
fs.fstat(a, a);
for (i = 0; i < 10; i++) { 
    fs.write(a, buffer, 0, buffer.length, bStats.size, a);
}
fs._close_();