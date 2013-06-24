var scrollerclose_timer = null;
var original_close = "";
var close_stack = new Array;


var scroller_object = 
{
    xml_comment_unique    : null,
    items          : null,

    element_height : 40,
    current_top    : 0,

    up             : null,
    dn             : null,

    x              : null,
    y              : null,

    direction      : 0,

    message_id     : null,

    selectedElement : null,

    scroll_page : function () {
        window.scrollTo(document.height, document.width);
    },

    scrollToBottom : function () {
        window.scrollTo(1,document.height);
    },
    
    set_xml : function(passed_xml) {
	alert(passed_xml);

    },

    scroll_width : function (width) {
	window.scrollBy(width,0);	

    },

    scroll_height : function (height) {
	window.scrollBy(0,height);

    },

    scroll_both : function (width,height) {
	window.scrollBy(width,height);	

    },
    
    findPos: function (obj) {
	    var curleft = curtop = 0;
	    if (obj.offsetParent) {
		    curleft = obj.offsetLeft;
		    curtop = obj.offsetTop;
		    while (obj = obj.offsetParent) {
			    curleft += obj.offsetLeft;
			    curtop += obj.offsetTop
		    }
	    }
	    return [curleft,curtop];
	    
    },
    
    init : function (node,message_id, old_message_id)
    {
 //       blog_object.unhighlight ();

    	if (this.message_id == message_id) {
    	return;
    	} 
       
        this.message_id = message_id;

        this.xml_comment_unique = xml_comment_unique;
	//alert(this.xml);

     //   this.x = e.pageX;
     //   this.y = e.pageY;
     	//var evt = document.createEvent("MouseEvents");
	
	//evt.initMouseEvent("click", true, true, window,0, 0, 0, 0, 0, false, false, false, false, 0, null);
     	//node.fireEvent("onclick",e);
	//var cancelled = node.dispatchEvent(evt);
	
	//alert(scroller_object.findPos(node)[0]);
     	//alert(parseInt(node.style.left));
	
	this.x = scroller_object.findPos(node)[0];
	this.y = scroller_object.findPos(node)[1];
	
        this.add_scroller (old_message_id, message_id);
    
        //window.scrollTo (document.height, document.width);

        if (old_message_id!='0') {
			setTimeout ("scroller_object.scroll_page ()", 100);

	} else {

		var height = ((this.element_height*5)+60);
		var width = 250;
			//alert(this.y + ' ' +height+ ' '+ window.innerHeight + ' '+window.innerWidth+ '  '+window.pageXOffset+ ' '+window.pageYOffset);
			//alert(this.y-window.pageYOffset);
			//alert(this.y + ' ' +window
		if (((this.y-window.pageYOffset)+height>window.innerHeight)&&(this.x+width>document.width)) {
			var right = (window.pageXOffset + window.innerWidth);
                        var scrollLeft = this.x;
                        var showingWidth = right-scrollLeft;
                        var diffWidth = (250-showingWidth);

			var bottom = (window.pageYOffset+window.innerHeight);
                        var scrollTop = this.y;
                        var showingHeight = (bottom - scrollTop);
                        var diffHeight = (height-showingHeight);

			setTimeout("scroller_object.scroll_both("+width+","+height+");",100);
		}
		else if ((this.y-window.pageYOffset)+height>window.innerHeight) {

			var bottom = (window.pageYOffset+window.innerHeight);
			var scrollTop = this.y;
			var showingHeight = (bottom - scrollTop);
			var diffHeight = (height-showingHeight+15);
					
			setTimeout("scroller_object.scroll_height("+diffHeight+");",100);
		}
		else if (this.x+width>document.width) {
			var right = (window.pageXOffset + window.innerWidth);
			var scrollLeft = this.x;
			var showingWidth = right-scrollLeft;
			var diffWidth = (250-showingWidth+15);

			setTimeout("scroller_object.scroll_width("+diffWidth+");",100);
		}


	}


        this.up.onmouseover = function (ev)
        {
            this.interval = self.setInterval ("scroller_object.scroll_dir ('" + ev.target.parentNode.id + "', 1)", 100);
        }

        this.up.onmouseout = function ()
        {
            clearInterval (this.interval);
        }

        this.dn.onmouseover = function (ev)
        {
            this.interval = self.setInterval ("scroller_object.scroll_dir ('" + ev.target.parentNode.id + "', 0)", 100);
        }

        this.dn.onmouseout = function ()
        {
            clearInterval (this.interval);
        }
    },

    pop_stack : function ()
    {
    	this.message_id = null;
        scroller_object.close_div (close_stack.pop ());
    },

    initialize_timer : function (node)
    {
        close_stack.push (node);
       
	scrollerclose_timer = setTimeout ("scroller_object.pop_stack ();", 400);
    },

    clear_timer : function ()
    {
	clearTimeout (scrollerclose_timer);
    },

    add_title : function (title_text)
    {
        var div = document.createElement ("div");
        div.setAttribute ("class", "drag");

        div.style.backgroundColor = "#000080";
        div.style.borderBottom    = "solid 1px white";
        div.style.color           = "white";
        div.style.height          = "20px";
        div.style.fontFamily      = "sans-serif";
        div.style.fontSize        = "12px";

        var close_icon = document.createElement ("img");
        close_icon.setAttribute ("onmouseover", "scroller_object.initialize_timer (this.parentNode.parentNode);");
        close_icon.setAttribute ("onclick", "clearTimeout (scrollerclose_timer); scroller_object.close_div (this.parentNode.parentNode);");
        close_icon.setAttribute ("onmouseout", "scroller_object.clear_timer ();");
        close_icon.setAttribute ("style", "float: right;");

        close_icon.src = "http://www.suprasphere.com/microblog/close.gif";
        //close_icon.src = "close.gif";

        div.appendChild (close_icon);
        div.appendChild (document.createTextNode (title_text));

        return div;
    },

    add_scroller : function (parent, message_id)
    {
        var p_content = document.getElementsByTagName ("body") [0];
        var scroller = document.createElement ("div");
        scroller.setAttribute ("id", message_id);
        scroller.setAttribute ("class", "scroll_container");
        scroller.setAttribute ("parent", parent);

        scroller.style.height = (this.element_height * 5) + 60 + "px";
        scroller.style.top    = this.y + "px";
        scroller.style.left   = this.x + "px";
        scroller.style.zIndex = max_zindex ++;

        this.up = document.createElement ("div");
        this.up.setAttribute ("class", "up_arrow");

        this.dn = document.createElement ("div");
        this.dn.setAttribute ("class", "dn_arrow");

        var elem_container = document.createElement ("div");

        elem_container.setAttribute ("class", "elem_container");
        elem_container.style.height = (this.element_height * 5) + "px";
        elem_container.appendChild (this.load_content ());

        //elem_container.addEventListener ('DOMMouseScroll', this.handle_wheel, false);

        scroller.appendChild (this.add_title (""));
        scroller.appendChild (this.up);
        scroller.appendChild (elem_container);
        scroller.appendChild (this.dn);

        p_content.style.height = p_content.style.offsetHeight + ((this.element_height * 5) + 60) + "px";
        p_content.style.width  = p_content.style.offsetWidth  + 250 + "px";

        p_content.style.top = p_content.style.top - ((this.element_height * 5) + 60) + "px";
        p_content.style.left = p_content.style.left - 250;

        p_content.appendChild (scroller);

    },

    add_xml : function ()
    {
        var new_xml = document.evaluate ("//textarea[@id='xml_to_add']", document, null, 6, null).snapshotItem (0);

        var xmldoc = new DOMParser ().parseFromString (xml_comment_unique, 'text/xml');
        var fragment = new DOMParser ().parseFromString (new_xml, 'text/xml');

        var thread = xmldoc.evaluate ("//thread",  xmldoc, null, 5, null).iterateNext ();

        xmldoc.appendChild (fragment);

    },

    remove_method_function : function () {
	alert('removing');

	var elem = document.getElementById("js_dom_event_monitor");
        //var body = document.getElementsByTagName ("body") [0];
            elem.parentNode.removeChild(elem);
	alert(document.getElementById("js_dom_event_monitor"));


    },

    set_xml_to_add : function (url) {

	//var pre_elem = document.getElementById("js_dom_event_monitor");
	//alert(pre_elem);

        var js_dom = document.createElement ("div");
	js_dom.setAttribute("id","js_dom_event_monitor");
	js_dom.setAttribute("event_url",url);
        var body = document.getElementsByTagName ("body") [0];
	body.appendChild(js_dom);
        //js_dom.setAttribute ("class", "monitor");

	var elem = document.getElementById("js_dom_event_monitor");
	//alert(elem.getAttribute("event_url"));

	//this.remove_method_function();

    },
	
    selectElement : function (message_id) {
	
        if(this.selectedElement!=null) {
	
	this.selectedElement.style.backgroundColor="white";
	} else {
	for(i=0;i<document.getElementsByTagName("div").length;i++) {
		document.getElementsByTagName("div")[i].style.backgroundColor="white";
		}
	}
	var currentElement = null;
	currentElement = document.getElementById(message_id);
	currentElement.style.backgroundColor="gainsboro";
        this.selectedElement = currentElement;
    },

    deleteMessage : function (message_id) {
	var messageToDelete = document.getElementById(message_id);
	messageToDelete.parentNode.removeChild(messageToDelete);
    },
    
    on_result_click : function(event, sphereId, message_id) {
    	if(!event) {
    		return;
    	}
    	this.selectElement(message_id);
    	var element = document.getElementById(message_id);
    	if(element.getAttribute("type")=='keywords') {
    		return;
    	}
    	if(event.button==2) {
			var elem = document.getElementById("show_ss_menu");
			if(elem!=null) {
				elem.parentNode.removeChild(elem);
			}
			var click = document.createElement("div");
			click.setAttribute("messageId",message_id);
			click.setAttribute("sphereId",sphereId);
			click.setAttribute("id","show_ss_menu");
			document.body.appendChild(click);		
    	}
    },

    on_mouse_click : function (message_id) {	
		var elem = document.getElementById("click_id");
		if(elem!=null) {
			elem.parentNode.removeChild(elem);
		}
	
		var click = document.createElement("div");
		click.setAttribute("messageId",message_id);
		click.setAttribute("id","click_id");

		var body = document.getElementsByTagName("body")[0];
		body.appendChild(click);	
    },

    on_mouse_for_keyword_name_not_unique : function (tag_name) {	
		var elem = document.getElementById("keyword_name_not_unique");
		if(elem!=null) {
			elem.parentNode.removeChild(elem);
		}
	
		var click = document.createElement("div");
		click.setAttribute("tag_name",tag_name);
		click.setAttribute("id","keyword_name_not_unique");

		var body = document.getElementsByTagName("body")[0];
		body.appendChild(click);	
    },

    on_mouse_for_keyword_name_to_show_in_preview : function ( unique_to_load, sphere_id_to_load ) {	
		var elem = document.getElementById("keyword_name_to_show_in_preview");
		if(elem!=null) {
			elem.parentNode.removeChild(elem);
		}
	
		var click = document.createElement("div");
		click.setAttribute("unique_to_load", unique_to_load);
		click.setAttribute("sphere_id_to_load", sphere_id_to_load);
		click.setAttribute("id","keyword_name_to_show_in_preview");

		var body = document.getElementsByTagName("body")[0];
		body.appendChild(click);	
    },
    
    highlightYellow : function(message_id) {
    	if(this.selectedElement!=null) {
			this.selectedElement.style.backgroundColor="white";
		} else {
			for(i=0;i<document.getElementsByTagName("div").length;i++) {
				document.getElementsByTagName("div")[i].style.backgroundColor="white";
			}
		}
		var currentElement = null;
		currentElement = document.getElementById(message_id);
		currentElement.style.backgroundColor="rgb(230,230,100)";
        this.selectedElement = currentElement;
    },

    on_dbl_click : function (message_id) {
	
	var elem = document.getElementById("dblclick_id");
	if(elem!=null) {
		elem.parentNode.removeChild(elem);
	}

	var dblclick = document.createElement("div");
	dblclick.setAttribute("messageId",message_id);
	dblclick.setAttribute("id","dblclick_id");
	
	var body = document.getElementsByTagName("body")[0];
	body.appendChild(dblclick);

	},

    scroll_to : function (message_id, browser_height) {
	var body = document.body;

	var currentElement = null;
	currentElement = document.getElementById(message_id);
	var target = currentElement.offsetTop;

	if(target < body.scrollTop-15 || target > body.scrollTop+browser_height-21) {
		var targetHeight = target-20;
		if(targetHeight<0) {
			window.scrollTo(0, 0);
		} else {
			window.scrollTo(0, targetHeight);
		}
	}
    },

    extractSelection : function () {
	var oldSelection = document.getElementById("selection");
	if(oldSelection!=null) {
		oldSelection.parentNode.removeChild(oldSelection);
	}
	var selection = document.getSelection();
	if(selection!=null & selection!="") {
		var elem = document.createElement("div");
		elem.setAttribute("id", "selection");
		elem.setAttribute("value", selection);
		var body = document.getElementsByTagName("body")[0];
		body.appendChild(elem);
	}
    },

    load_content : function ()
    {
        var i = 0;

        var container = document.createElement ("div");
        container.setAttribute ("id", "container_" + this.message_id);

        container.setAttribute ("class", "elements");

        var xmldoc = new DOMParser ().parseFromString (this.xml_comment_unique, 'text/xml');

        var messages = xmldoc.evaluate ("//thread/email/response_id[@value='" + this.message_id + "']", xmldoc, null, 5, null);
        var xsize    = xmldoc.evaluate ("//thread/email/response_id[@value='" + this.message_id + "']", xmldoc, null, 6, null).snapshotLength;
       
        while (result = messages.iterateNext ()) {
            var message_id = result.parentNode.getElementsByTagName ("message_id") [0].getAttribute ("value");
		try {
            var address = result.parentNode.getElementsByTagName ("address") [0].getAttribute ("value");
		} catch (err)
		{
		}
            var replies = xmldoc.evaluate ("//thread/email/response_id[@value='" + message_id + "']", xmldoc, null, 6, null).snapshotLength;

            var div = document.createElement ("div");
            var subj_div = document.createElement ("div");

            subj_div.setAttribute ("id", "subj_" + message_id);
            subj_div.style.height = this.element_height + "px";
            subj_div.setAttribute ("class", "subject");
	    subj_div.setAttribute("address",address);
            subj_div.setAttribute("onclick","scroller_object.set_xml_to_add('"+subj_div.getAttribute("address")+"');");
            var subdiv = document.createElement ("div");
            subdiv.setAttribute ("id", "subdiv_" + message_id);
            subdiv.setAttribute ("class", "content");

            var content = result.parentNode.getElementsByTagName ("body") [0].firstChild.data;
            var subject = result.parentNode.getElementsByTagName ("subject") [0].getAttribute ("value");
            
            var read_more = document.createElement ("a");
            read_more.setAttribute ("href", "#");
            read_more.setAttribute ("onclick", "comment_object.show_comment (" + this.x +", " + this.y + ", '" + escape (subject) + "', '" + escape (content) + "');");
            read_more.setAttribute ("class", "read_more");
            read_more.appendChild (document.createTextNode ("[read entire comment]"));

            var view_replies = document.createElement ("a");
            view_replies.setAttribute ("id", "reply_" + message_id);
            view_replies.setAttribute ("href", "#"); 
            view_replies.setAttribute ("onclick", "scroller_object.init (event, '" + message_id + "', '" + this.message_id + "');");
            view_replies.setAttribute ("class", "view_replies");

            subdiv.style.width = "100%;";

            subdiv.style.display = "none";

            subdiv.appendChild (read_more);

            var reply_text;

            if (replies > 1) {
                reply_text = "[view " + replies + " replies]";
                view_replies.appendChild (document.createTextNode (reply_text));
                subdiv.appendChild (view_replies);
            } else if (replies == 1) {
                reply_text = "[view " + replies + " reply]";
                view_replies.appendChild (document.createTextNode (reply_text));
                subdiv.appendChild (view_replies);
            }

            var ex_div = document.createElement ("div");
            ex_div.setAttribute ("class", "expansion");
            ex_div.setAttribute ("id", "ex_div_" + message_id);
            ex_div.style.display = "none";

            div.setAttribute ("onmouseover", "scroller_object.show_snippit ('" + message_id + "');");
            div.setAttribute ("onmouseout", "scroller_object.hide_snippit ('" + message_id + "');");

            subj_div.appendChild (document.createTextNode (subject.substring (0, 40)));
            subj_div.appendChild (subdiv);

            ex_div.appendChild (document.createTextNode (content.substring (0, 100)));

            if (content.length > 100) {
                ex_div.appendChild (document.createTextNode (" ..."));
            }

            div.appendChild (subj_div);
            div.appendChild (ex_div);
            container.appendChild (div);
        }

        this.items = xsize;
        return container;
    },

    scroll_dir : function (id, dir)
    {
        if ((!dir && this.current_top <= (-1 * (this.items - 2) * this.element_height)) || (dir && this.current_top >= 0)) {
            return;
        }

        this.direction = dir;
        this.animate (id);
    },

    handle_wheel : function (event)
    {
        event.preventDefault ();

        var scroll_id = "";
        var node = event.target;

        while (scroll_id.substr (0, 10) != "highlight_") {
            node = node.parentNode;

            if (node.id) {
                scroll_id = node.id;
            }
        }

        var delta = -event.detail / 3;

        if (delta == -1) {
            scroller_object.scroll_dir (scroll_id, 0)
        } else {
            scroller_object.scroll_dir (scroll_id, 1);
        }
    },

    animate : function (id)
    {
        if (!this.direction) {
            this.current_top -= this.element_height;
        } else {
            this.current_top += this.element_height;
        }

        var container = document.getElementById ("container_" + id);

        container.style.top = this.current_top + "px";
    },

    close_div : function (start_node)
    {
        if (original_close == "") {
            original_close = start_node;
        }

        var node = start_node;

        var parent = start_node.id;

        var children = document.evaluate ("//div[@parent='" + parent + "']", document, null, 6, null).snapshotLength;

        if (children == 0) {
            node.parentNode.removeChild (node);

            if (original_close.id == node.id) {
                original_close = "";
                return;
            }
            this.close_div (original_close);
        } else {
            node = document.evaluate ("//div[@parent='" + parent + "']", document, null, 6, null).snapshotItem (0);
            this.close_div (node);
        }
    },

    show_snippit : function (snip_id)
    {
        var div = document.getElementById ("ex_div_" + snip_id);

        div.style.display = "block";

        var subj_div = document.getElementById ("subj_" + snip_id);
        subj_div.style.backgroundColor = "#f0f6cd";

        var subdiv = document.getElementById ("subdiv_" + snip_id);
        subdiv.style.display = "block";
    },

    hide_snippit : function (snip_id)
    {
        var div = document.getElementById ("ex_div_" + snip_id);
        div.style.display = "none";

        var subj_div = document.getElementById ("subj_" + snip_id);
        subj_div.style.backgroundColor = "";

        var subdiv = document.getElementById ("subdiv_" + snip_id);
        subdiv.style.display = "none"; 
    },

    ouput_sent : function ()
    {
        var ouput_text = document.getElementById ("output_text").value;

        debug_object.show_bug (output_text);
    },

    input_ready : function ()
    {
        var input_text = document.getElementById ("input_text").value;

        debug_object.show_bug (input_text);
    }
}
