
// CounterStriker

var allCounters = $('.counterstriker');

if(allCounters.length) {
    allCounters.each(function(i) {
        var $el = $(this);

        var options = $el.attr();
        options.target = '#'+options.id;

        helpers.counter( options );
    });
}

// Footer

var workdata = [];

var prepareData = function(feed) {
    var currentType;

    $.each(feed, function(index, item) {
        var typeNow  = currentType,
            type     = item['gsx$type']['$t'],
            linkname = item['gsx$linkname']['$t'],
            linkurl  = item['gsx$linkurl']['$t'],
            target   = item['gsx$dontopeninnewwindow']['$t'];

        if(index === 0) {
            currentType = type;
        } else {
            if(type !== '') {
                currentType = type;
            }
        }
        if(currentType !== typeNow) {
            workdata.push({
                type: type,
                items: []
            });
        }
        workdata[workdata.length - 1].items.push({
            linkname: linkname,
            linkurl: linkurl,
            target: target
        });
    });
};

var createLinks = function($targetContainer, workdata) {

    var htmlLinks = [];

    $.each(workdata, function(index, item) {
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
    });
    htmlLinks = htmlLinks.join(' | ');
    $targetContainer.html(htmlLinks);
};

var JSONcallback = function(data) {
    prepareData(data.feed.entry);
    createLinks($('#talinks'), workdata[0].items);
    createLinks($('#tapartner'), workdata[1].items);
};

var spreadsheet = 'https://spreadsheets.google.com/feeds/list/1uTRp8fPs-f_zbWgsufPHnRq6U62c1Y8qulgf94VNAbc/od6/public/values?alt=json-in-script';
if(helpers.lang() !== 'de') {
    spreadsheet = 'https://spreadsheets.google.com/feeds/list/1VEcNBsunOZqmI3Iq_VjfeQFiZmzk9oR-GB2uGEp9xQo/od6/public/values?alt=json-in-script';
}

$.ajax({
    url: spreadsheet,
    dataType: 'JSONP',
    jsonpCallback: 'JSONcallback',
    type: 'GET'
});
