console.log('https://github.com/schamane/node-syslog');
console.log('npm install node-syslog');

var Syslog = require('node-syslog').Syslog;

Syslog.init("node-syslog", Syslog.LOG_PID | Syslog.LOG_ODELAY, Syslog.LOG_INFO);
Syslog._log_();