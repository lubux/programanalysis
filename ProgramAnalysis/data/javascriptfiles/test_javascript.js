
function person(first, last){
	this.firstname = first;
	this.lastname = last;
	this.func = function(address){
		this.address = address;
	};
}
var c = "firstname"
var pers = new person("hans", "peter");
var b = pers;
var d = b[c];
funA();
pers.func("bahnhofstrasse");
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