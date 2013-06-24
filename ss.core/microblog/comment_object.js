var comment_object = 
{
    x : null,
    y : null,
    close : null,

    show_comment : function (x, y, subject, content)
    {
        this.x = x;
        this.y = y;

        var body = document.getElementsByTagName ("body") [0];

        var div = document.createElement ("div");
        div.setAttribute ("class", "ss_comment");
        div.style.top = this.y + "px";
        div.style.left = this.x + "px";
	div.style.zIndex = max_zindex++;
        div.id = "ss_comment";

        var close_img = document.createElement ("img");
        close_img.setAttribute ("src", "close.gif");
        close_img.setAttribute ("alt", "Close");
        close_img.setAttribute ("style", "float: right");
        close_img.setAttribute ("onmouseover", "comment_object.close_me(this);");
        close_img.setAttribute("onmouseout","comment_object.cancel_close();");

        div.appendChild (close_img);

        var subdiv = document.createElement ("div");
        subdiv.style.clear = "both";

        div.appendChild (subdiv);

        var subj_div = document.createElement ("div");
        subj_div.appendChild (document.createTextNode (unescape (subject)));
        subj_div.setAttribute ("class", "c_subject");

        var cont_div = document.createElement ("div");
        cont_div.appendChild (document.createTextNode (unescape (content)));
        cont_div.setAttribute ("class", "c_content");

        subdiv.appendChild (subj_div);
        subdiv.appendChild (cont_div);

        body.appendChild (div);
    },

    close_me : function (node) {

	comment_object.close = setTimeout(function(){comment_object.close_comment(node);},400);
	
   },   	
	
    close_comment : function (node) {

 	node.parentNode.parentNode.removeChild(node.parentNode);	

    },
    cancel_close : function () {

	clearTimeout(comment_object.close);
	
    },

    register_click : function (comment_id, message_id) {
	var previousSupport = document.getElementById("supportDiv");
	if(previousSupport!=null) {
		document.body.removeChild(previousSupport);
	}
	var commentElem = document.getElementById(comment_id);

     	var supportDiv = document.createElement("div");
	supportDiv.setAttribute("id", "supportDiv");
	supportDiv.setAttribute("message_id", message_id);
	document.body.appendChild(supportDiv);

    } 


}
