var popup = window.open('', '_blank', 'toolbar=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=yes,copyhistory=no,width=360,height=160');
popup.document.open();
popup.document.write('<html><head>');
popup.document.write('<title>' + name + '</title>');
popup.document.write('<base href="https://www.ergon.ch/" />');
popup.document.write('<link rel="stylesheet" type="text/css" href="fileadmin/templates/style.css" />');
popup.document.write('</head><body><div class="staff-short" style="padding: 12px 36px;">');
popup.document.write('<h3>' + name + '</h3>');
popup.document.write('<div style="float: left; font-size: 11px; line-height: 19px;">');
popup.document.write('E-Mail:<br />');
popup.document.write('Phone:<br />');
if (mobile != null && mobile.length > 0) {
	popup.document.write('Mobile:<br />');
}
popup.document.write('Fax:</div>');
popup.document.write('<div class="description">');
popup.document.write('<p><a href="mailto:' + email + '@ergon.ch">' + email + '@ergon.ch</a></p>');
if (phone.length == 3) {
	phone = '+41 44 268 8' + phone.substring(0, 1) + ' ' + phone.substring(1);
} else if (phone == null || phone.length < 3) {
	phone = '+41 44 268 89 00';
}
popup.document.write('<p>' + phone + '</p>');
if (mobile != null && mobile.length > 0) {
	popup.document.write('<p>' + mobile + '</p>');
}
popup.document.write('<p>+41 44 261 27 50</p>');
popup.document.write('</div></div></div></body></html>');
popup.document._close_();