var numbers = [1, 2, 3, 4, 5]
var result = 0;
function sum(number){
    result += numbers;
}
numbers.forEach(sum);
var increment = numbers._map_();