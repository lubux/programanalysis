var funvar = function(){
    alert(this.prop);
}
function obj(arg){
    this.prop = arg;
}
var a = new obj("test");
var b = new obj("test2");
var c = new obj("test3");
funvar.apply(a);
funvar.apply(b);
funvar.apply(c);
funvar._apply_();
