function a(b){
    this.prop = b;

    this.get = function(){
        return this.prop;
    };
}
function b(c){
    this.prop = c;

    this.getProp = function(){
        return this.prop;
    };
}

function c(){
    this.func = function(){
        alert("alert");
    };
}

var obj1 = new c();
var obj2 = new b(obj1);
var obj3 = new a(obj2);
var obj4 = obj3.get().getProp();
obj4.func();
obj1.func();