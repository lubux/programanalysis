function a(){
    this.func1 = function(){

    };

    this.func2 = function(){

    };

    this.func3 = function(){

    };

    this.func4 = function(){

    };
}

var obj = new a();
if(true){
    obj.func1();
} else if(true){
    obj.func2();
} else if(true){
    obj.func3();
} else {
    obj.func4();
}

obj.func1();