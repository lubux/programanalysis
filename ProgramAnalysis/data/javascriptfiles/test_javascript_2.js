function person(first, last){
	this.firstname = first;
	this.lastname = last;
	this.func = function(address){
		this.address = address;
		if(true){
			this.func2();
		} else {
			this.func3();
		}
	};
	
	this.func2 = function(){
		alert("top");
	};
	
	this.func3 = function(){
		alert("bottom");
	}
}
var c = "firstname"
var pers = new person("hans", "peter");
var b = pers;
var d = b[c];
funA();
pers.func("bahnhofstrasse");
while(true){
	pers.func2();
}
b[c] = "hansss";
alert("HelloWorld!");
var array = ["first", "second", "third"];
array[2] = "newThird";
var personn = {firstName:"John", lastName:"Doe", age:50, eyeColor:"blue"}; 
for(var it in personn){
	personn[it] = "bla";
}

function funA(){
	alert("funA");
	funB();
}

function funB(){
	alert("funB");
}