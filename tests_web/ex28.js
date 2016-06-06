var $e = $("#seitenkopf_miniwetter");
var codeSegments = $e.find(".weather-search__forecast-col");
$e.find(".temperature").text(data.weatherNow + "\u00b0");
$e.find(".weatherlabel").text(unescape(data.label));
$e._find_(".temp .ovl_overlay").attr("class", "ovl_overlay " + symbolCodes[data.symbolCodeNow]).attr("title", unescape(data.symbolTextNow));
/** @type {number} */
var i = 0;
for (;i < codeSegments.length;i++) {
  var $page = $(codeSegments[i]);
  var entry = data.temps[i];
  $page.find(".ovl_overlay").attr("class", "ovl_overlay " + symbolCodes[entry.symbolCode]).attr("title", unescape(entry.symbolText));
  $page.find(".weather-search__forecast-day").text(entry.day);
  $page.find(".min").text(entry.minTemp);
  $page.find(".max").text(entry.maxTemp);
}