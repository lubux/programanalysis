
/* <![CDATA[ */
var _sf_startpt=(new Date()).getTime();// Chartbeat
var WEMF_section='/home';
scz.special='ressort';

document.addEventListener('DOMContentLoaded',function(){
    if(typeof(dataLayer) != 'undefined'){
        dataLayer.push({'event': 'wemfSection'}); /* GTM Pageview */
    }
});

function getWEMF(caller){
    if(typeof(dataLayer) != 'undefined'){
        dataLayer.push({'event': 'wemfSection'}); /* GTM Pageview */
    }
}

function setDefaultProperties(category,agof,swipe){delomni();scz.prop4='Nachrichten aus der Schweiz und aller Welt - Blick ';scz.pageName='home';scz.channel='home';if(category){scz.prop6=category;}else{scz.prop6='ressort';}if(swipe){scz.prop12=swipe;}scz.prop13=scz.special;if(agof){scz.prop7=agof;}else{scz.prop7='/home';}if(category=='artikel'||category=='video'||category=='slideshow'||category=='fehler'){scz.prop17=scz.prop6+':';};scz.trackingServer='omni.blick.ch';scz.hier1=scz.pageName;}



getomni('ressort','');
$(document).ready(function(){$(document).delegate('.trackCustomLink','click',function(event){event.stopPropagation();event.preventDefault(); trackCustomLink(this,$(this).data('rel'));});});
/* ]]> */
