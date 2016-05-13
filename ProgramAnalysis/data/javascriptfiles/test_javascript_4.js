/**
 * Created by cedri on 5/13/2016.
 */
function person(name, age){
    this.name = name;
    this.age = age;
    this.func = function(){
        this.func2();
    };
    this.func2 = function(){
        alert("func2");
    }
}
var per = new person("Bruno", 59);
per.func();


function globalFunc(pers){
    pers.func2();
}

function globalFunc2(){
    var per = new person("Hans-Peter", 38);
    globalFunc(per);
}

function globalFunc3(){
    var per = new person("Hans-Peter", 38);
    globalFunc(per);
}