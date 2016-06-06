var ar = ["a", "b", "c", "d", "e"];
var array2 = ["f", "g"];
function test(arg){
    return arg == "c";
}
var f = ar.find(test);
var g = ar.indexOf("d");
var array3 = ar._concat_();