function a(index, item) {
    var target = ' target="_blank"';
    if(item.target) {
        target = ' target="_self"';
    }

    item.linkurl = item.linkurl.replace('http://', '');
    item.linkname = item.linkname.replace('ä', '&auml;');
    item.linkname = item.linkname.replace('ö', '&ouml;');
    item.linkname = item.linkname.replace('ü', '&uuml;');
    item.linkname = item.linkname.replace('Ä„', '&Auml;');
    item.linkname = item.linkname.replace('Ö–', '&Ouml;');
    item.linkname = item.linkname.replace('Üœ', '&Uuml;');
    item.linkname = item.linkname.replace('é', '&eacute;');

    htmlLinks.push('<a href="http://' + item.linkurl + '"' + target + '>' + item.linkname + '</a>');
}