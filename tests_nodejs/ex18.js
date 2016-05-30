var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : 'qaz#123',
  database : 'test'
});

connection.connect();

//fetch data from table
connection.query('SELECT * from users', function(err, rows, fields) {
  if (err) { 
  	throw err;
  }
  console.log(rows);
});

connection._end_();