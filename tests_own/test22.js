var arr = ["These" , "are", "the", "words", "of", "a", "sentence", "."];
var text = arr.join(" ");
if(text.length > 10){
    var a = arr.slice(0, 4);
} else {
    var b = arr.shift();
}
arr.push("..");
arr._filter_();
