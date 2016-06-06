var location = new Object();
if (location.pathname === "/core" || location.pathname.indexOf("/core/") === 0) {
	var images = document.getElementsByTagName("img");
	for (var i = 0; i < images.length; ++i) {
		var image = images[i];
		if (image.src && image.src.indexOf("/core/") > -1) {
			image.src = image.src.replace("/core/", "/");
		}
	}
	var as = document.getElementsByTagName("a");
	for (var i = 0; i < as.length; ++i) {
		var a = as[i];
		if (a.href && a.href.indexOf("/core/") > -1) {
			a.href = a.href.replace("/core/", "/");
		}
	}
}
if (location.pathname === "/psi" || location.pathname.indexOf("/psi/") === 0) {
	var images = document.getElementsByTagName("img");
	for (var i = 0; i < images.length; ++i) {
		var image = images[i];
		if (image.src && image.src.indexOf("/psi/") > -1) {
			image.src = image.src.replace("/psi/", "/");
		}
	}
	var as = document.getElementsByTagName("a");
	for (var i = 0; i < as.length; ++i) {
		var a = as[i];
		if (a.href && a.href.indexOf("/psi/") > -1) {
			a.href = a.href.replace("/psi/", "/");
		}
	}
}
if (location.pathname === "/elina" || location.pathname.indexOf("/elina/") === 0) {
	var images = document.getElementsByTagName("img");
	for (var i = 0; i < images.length; ++i) {
		var image = images[i];
		if (image.src && image.src.indexOf("/elina/") > -1) {
			image.src = image.src.replace("/elina/", "/");
		}
	}
	var as = document._getElementsByTagName_();
}