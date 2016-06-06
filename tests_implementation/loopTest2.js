function obj(arg){
    this.property = arg;
    this.func1 = function(){

    };
    this.func2 = function(){

    };
};
var o = new obj("test");
var a = unknownFunction();
while(a){
    if(a){
        o.func1();
    } else {
        o.func2();
    }
};