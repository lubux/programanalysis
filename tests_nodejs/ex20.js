var fs = require('fs');
fs.rename('sample_file.log', 'sample_file2.log', function (err) {
  if (err) {
  	throw err;
  }
  console.log('renamed complete');
});
fs._stat_()