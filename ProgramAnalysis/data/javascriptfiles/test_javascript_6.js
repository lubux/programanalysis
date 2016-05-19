/**
 * Created by cedri on 5/16/2016.
 */

function Meal(name, beverage, beverage2){
    this.name = name;

    this.beverage = beverage;

    this.beverage2 = beverage2;

    this.eat = function(){
        alert("Eating");
    };

    this.drink = function(){
        alert("Drinking");
    };

    this.getBeverage = function(){
        if(true){
            return this.beverage;
        } else {
            return this.beverage2;
        }
    }
}

function Beverage(name){
    this.name = name;

    this.drink = function(){
        alert("drinking");
    };
}
var beverage = new beverage("Coke");
var beverage2 = new beverage("Fanta");
var burger = new Meal("burger & fries", beverage, beverage2);
burger.getBeverage().drink();
var beverage3 = (new Meal("pizza", beverage, beverage2)).getBeverage();
beverage3.drink();

if(true){
    burger.eat();
    if(true){
        burger.eat();
        if(true){
            burger.eat();
        } else {
            burger.drink();
        }
    } else {
        burger.drink();
        if(true){
            burger.eat();
        } else {
            burger.drink();
        }
    }
} else  {
    burger.drink();
    if(true){
        burger.eat();
        if(true){
            burger.eat();
        } else {
            burger.drink();
        }
    } else {
        burger.drink();
        if(true){
            burger.eat();
        } else {
            burger.drink();
        }
    }
}
burger.predictionfunction();