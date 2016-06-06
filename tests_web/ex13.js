var popup = window.open('', '_blank', 'toolbar=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=yes,copyhistory=no,width=360,height=160');
popup.document.open();
popup.document.write('');
popup.document.write(name );
popup.document.write('');
popup.document.write('');
popup.document.write('');
popup.document.write(name);
popup.document.write('');
popup.document.write('');
popup.document.write('');
if (mobile != null && mobile.length > 0) {
	popup.document.write('');
}
popup.document.write('');
popup.document.write('');
popup.document.write('' + email + '' + email + '');
if (phone.length == 3) {
	phone = '' + phone.substring(0, 1) + ' ' + phone.substring(1);
} else if (phone == null || phone.length < 3) {
	phone = '';
}
popup.document.write('<p>' + phone + '</p>');
if (mobile != null && mobile.length > 0) {
	popup.document.write('<p>' + mobile + '</p>');
}
popup.document.write('<p></p>');
popup.document.write('</div></div></div></body></html>');
popup.document._close_();