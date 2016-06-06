(function()  {
        var httpRequest;
        document.getElementById("ajaxButton").onclick = (function()  {
                makeRequest("test.html");
            }
        );
        function makeRequest(url)  {
            httpRequest = new XMLHttpRequest();
            if (! httpRequest)  {
                alert("Giving up :( Cannot create an XMLHTTP instance");
                return false;
            }
            httpRequest.onreadystatechange = alertContents;
            httpRequest._();
        }
        ;
    }
)();