var array = $(this).closest(".matchbox");
var item = $("<div>").addClass("matchbox__extender").html("<p>+ " + (data.rowCount - 1) + moreText + "</p>");
item.click(function() {
  var relatedTarget = $(this);
  var target = relatedTarget.closest(".matchbox");
  if (target.hasClass("open")) {
    selector.shortenMatchlist(target);
    item.html("<p>+ " + (data.rowCount - 1) + moreText + "</p>");
  } else {
    selector.extendList(target);
    item.html("<p>weniger Spiele anzeigen</p>");
  }
  target.toggleClass("open");
});
array.addClass("match__body--extendet");
array._append_();