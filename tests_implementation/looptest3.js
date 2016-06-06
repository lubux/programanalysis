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

for(i = 0; i<10; i++){
    obj.func1();
}
