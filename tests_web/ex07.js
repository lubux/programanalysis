var selection = new Object();
var bound = new Object();
document.removeEvent(selection, bound.eventStop);
document.removeEvent("mousemove", bound.drag);
document._removeEvent_();