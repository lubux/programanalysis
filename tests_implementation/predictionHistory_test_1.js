/**
 * Created by cedri on 5/19/2016.
 */
var xmlhttp = new XMLHttpRequest();
var url = "myTutorials.txt";
xmlhttp.onreadystatechange = function()  {
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200)  {
        var myArr = JSON.parse(xmlhttp.responseText);
        myFunction(myArr);
    }
}
;
xmlhttp.predictionfunction();