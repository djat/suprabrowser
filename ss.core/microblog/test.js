var test = {

 init: function () {


     var body = document.getElementsByTagName ("body") [0];

     var div = document.createElement ("div");

     div.setAttribute ("style", "border: solid 1px black; height: 200px; width: 200px; top: 50; left: 50");
     div.appendChild (document.createTextNode (xml));

     body.appendChild (div);
	}
}
