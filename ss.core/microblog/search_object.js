var search_object = {

    show : function (sphere_id, message_id,keywords)
    {
    	var flyer = document.getElementById('flyer');
    	if(flyer!=null) {
    		flyer.parentNode.removeChild(flyer);
    	}
       var elem = document.getElementById("showSphere");
	if(elem!=null) {
		elem.parentNode.removeChild(elem);
	}
	 
	var click = document.createElement("div");
	click.setAttribute("messageId",message_id);
        click.setAttribute("sphereId",sphere_id);
        click.setAttribute("keywords",keywords);
	click.setAttribute("id","showSphere");

	var body = document.getElementsByTagName("body")[0];
	body.appendChild(click);
    },

    open : function (url)
    {
        var elem = document.getElementById("showURL");
	if(elem!=null) {
		elem.parentNode.removeChild(elem);
	}
	
	var click = document.createElement("div");
	click.setAttribute("url",url);
	click.setAttribute("id","showURL");

	var body = document.getElementsByTagName("body")[0];
	body.appendChild(click);
    }
}
