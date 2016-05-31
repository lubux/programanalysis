var array = ["a", "b", "c", "d", "e"];
var array2 = ["f", "g"];
function test(arg){
    return arg == "c";
}
var f = array.find(test);
var g = array.indexOf("d");
var array3 = array._concat_(array2);