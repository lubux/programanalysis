var a = 6;
if(Number.isFinite(a)){
    a += 2;
} else {
    a = 0;
}
if(Number._isNaN_(a)){
    alert("It's NaN");
}