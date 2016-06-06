/**
 * Created by cedri on 5/13/2016.
 */
function book(title, year){
    this.title = title;
    this.year = year;

    this.read = function(){
        alert("I'm reading " + title);
    }
}

var bible = new book("bible", 0);
bible.read();

function globalfunc(){
    var touchOfClass = new book("Touch Of Class", 2007);
    touchOfClass.read();
    globalfunc2();
}
function globalfunc2(){
    var dalgo = new book("Datenstrukturen & Algorithmen", 2009);
    dalgo.read();
    globalfunc();
}

function globalfunc3(){
    var romeojuliet = new book("Romeo & Juliet", 1856);
    romeojuliet.read();
    globalfunc();
}