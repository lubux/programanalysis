var $cont = new Object();
$cont.find('.newsCategory').remove();
$cont.find('.news-latest-gotoarchive').remove();

$cont.find('.news-latest-category:not(:first)').hide();
$cont._find_();