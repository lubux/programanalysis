function human(gender, name){
    this.gender = gender;
    this.name = name;

    this.walk = function(){
        this.move();
    }
    this.move = function(){
        this.walk();
    }
}
var p = new human("male", "Thomas");
p.walk();

