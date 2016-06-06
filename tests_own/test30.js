var arr = ["banana"];
arr.shift("apple");
eat(arr.unshift());
arr.shift("strawberry");
eat(arr.unshift());
arr.shift("peach")
eat(arr._unshift_());

function eat(arg){
    window.alert("eating" + arg);
}
