var advanced_object =
{   
    performAdvance : function() {
    	var elem = document.getElementById("advance");
    	if(elem!=null) {
			elem.parentNode.removeChild(elem);
		}
    	var button = document.getElementById("spoiler_button");
    	var click = document.createElement("span");
		click.setAttribute("id","advance");
    	if(button.innerHTML=='+') {
    		click.setAttribute("action","expand");
    		button.innerHTML = "-";
    	} else {
    		click.setAttribute("action","collapse");
    		button.innerHTML = "+";
    	}
		document.body.appendChild(click);
    },
    
    search : function() {
    	var text = document.getElementById("to_search").value;
    	trimtext = text;
    	if(trimtext!=null) {
    		while(trimtext.indexOf(' ')>-1) {
    			trimtext = trimtext.replace(' ', '');
    		}
    	}
    	if(text==null || trimtext=='') {
    		alert('String query should be not empty!');
    		return;
    	}
    	var elem = document.getElementById("repeat_search");
    	if(elem!=null) {
			elem.parentNode.removeChild(elem);
		}
		var click = document.createElement("span");
		click.setAttribute("id","repeat_search");
		click.setAttribute("text", text);
		document.body.appendChild(click);
    },
    
    search_from_field : function(event) {
    	var eventObject = window.event;
    	if(!window.event) {
    		eventObject = event;
    	}
    	if(eventObject.keyCode!=13) {
    		return;
    	}
    	this.search();
    }, 
    
    fillAdvancedBlock : function(filler) {
    	var spoiler = document.getElementById('spoiler');
    	spoiler.innerHTML = filler;
    }, 
    
    clearSpoiler : function() {
    	var spoiler = document.getElementById('spoiler');
    	spoiler.innerHTML = "";
    },
    
    selectAll : function(classname) {
    	var inputs = document.getElementsByTagName('input');
    	for(var i=0; i<inputs.length; i++) {
    		if(!inputs[i].className) { 
    			continue;
    		}
    		if(inputs[i].className==classname) {
    			inputs[i].checked=true;
    		}
    	}
    },
    
    deselectAll : function(classname) {
    	var inputs = document.getElementsByTagName('input');
    	for(var i=0; i<inputs.length; i++) {
    		if(inputs[i].className) { 
    			if(inputs[i].className==classname) {
    				inputs[i].checked=false;
    			}
    		}
    	}
    },
    
    selectAllTypes : function() {
    	this.selectAll('type');
    }, 
    
    deselectAllTypes : function() {
    	this.deselectAll('type');
    },
    
    selectAllContacts : function() {
    	this.selectAll('contact');
    }, 
    
    deselectAllContacts : function() {
    	this.deselectAll('contact');
    },
    
    selectAllSpheres : function() {
    	this.selectAll('sphere');
    }, 
    
    deselectAllSpheres : function() {
    	this.deselectAll('sphere');
    }
    
}