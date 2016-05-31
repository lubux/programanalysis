var dns = require('dns');

dns.resolve4('www.google.com', function (err, addresses) {
  if (err) {
  	throw err;
  } 
  console.log('addresses: ' + JSON.stringify(addresses));
});

dns._resolve4_()