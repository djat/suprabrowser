var research_object = {

baseUrl : "/",
req : null,
clubId : null,
keyword : null,
sType : null,
eventLeft : null,
eventTop : null,
difX : 0,
difY : 0,
READY_STATE_UNINITIALIZED : 0,
READY_STATE_LOADING : 1,
READY_STATE_LOADED : 2,
READY_STATE_INTERACTIVE : 3,
READY_STATE_COMPLETE : 4,

addMark : function(event, keyword) {
	var node = document.createElement("div");
	node.setAttribute("id", "research");
	node.setAttribute("value", keyword);
	node.setAttribute("x", event.clientX);
	node.setAttribute("y", event.clientY);
	document.body.appendChild(node);
	
	this.eventLeft = event.clientX+window.pageXOffset;
	this.eventTop = event.clientY+window.pageYOffset;
},

addClickMark : function(keyword) {
	var node = document.createElement("div");
	node.setAttribute("id", "research_click");
	node.setAttribute("value", keyword);
	document.body.appendChild(node);
},

closeFlyer : function() {
	var flyer = document.getElementById('flyer');
	flyer.parentNode.removeChild(flyer);
},
		
stateChange : function(respData) {
while(respData.indexOf('&#39;')>-1) {
	respData = respData.replace('&#39;', '\'');
}
while(respData.indexOf('&quot;')>-1) {
	respData = respData.replace('&quot;', '"');
}
	var flyer = document.createElement('div');
	flyer.className='flyer';
	flyer.id = 'flyer';
	
	var wHeight = window.innerHeight+window.pageYOffset;
	var wWidth = window.innerWidth+window.pageXOffset;
	if(wHeight-this.eventTop<300 && this.eventTop>300) {
		this.eventTop = this.eventTop - 310;
	} else {
		this.eventTop = this.eventTop + 10;
	}
	flyer.style.top = this.eventTop+'px';
	if(wWidth-this.eventLeft<600 && this.eventLeft>600) {
		this.eventLeft = this.eventLeft - 610;
	} else {
		this.eventLeft = this.eventLeft + 10;
	}
	flyer.style.left = this.eventLeft+'px';
	flyer.onmousedown=this.addDragger;
	document.body.appendChild(flyer);

	var flyer_header = document.createElement('span');
	flyer_header.className = 'flyer_header';
	flyer_header.onmousedown = this.addDragger;
	flyer_header.id='flyer_header';

	var closer = document.createElement('img');
	closer.className='flyer_closer';
	closer.id='flyer_closer';
	closer.onclick = this.closeFlyer;
	closer.onmouseover = this.highlightCloser;
	closer.onmouseout = this.unhighlightCloser;
	closer.onmousedown = this.blackCloser;
	
	var flyerBody = document.createElement('div');
	flyerBody.className = 'flyer_body';
	flyerBody.id='text_container';
		
	flyer.appendChild(flyer_header);
	flyer.appendChild(closer);
	flyer.appendChild(flyerBody);
	
	var text_container = document.getElementById('text_container');
	text_container.innerText = ' ';
	text_container.innerHTML = respData;
},
			
highlightCloser : function(){
	var closer = document.getElementById('flyer_closer');
	closer.style.backgroundColor = 'lightblue';
},
		
unhighlightCloser : function(){
	var closer = document.getElementById('flyer_closer');
	closer.style.backgroundColor = 'darkblue';
},
		
blackCloser : function(){
	var closer = document.getElementById('flyer_closer');
	closer.style.backgroundColor = 'darkgray';
},
		
addDragger : function(event){
	var header = document.getElementById('flyer_header');
	var flyer = document.getElementById('flyer');
	var eventObject;
	if(event) {
		eventObject = event;
	} else if(window.event) {
		eventObject = window.event;	
	} 
	this.difX = eventObject.clientX+window.pageXOffset-flyer.style.left.replace('px','');
	this.difY = eventObject.clientY+window.pageYOffset-flyer.style.top.replace('px','');
	header.onmousemove=research_object.dragFlyer;
	header.onmouseout=research_object.releaseDragger;
	header.onmouseup=research_object.releaseDragger;
	flyer.onmousemove=research_object.dragFlyer;
	flyer.onmouseout=research_object.releaseDragger;
	flyer.onmouseup=research_object.releaseDragger;
},
		
dragFlyer : function(event){
	var eventObject;
	if(event) {
		eventObject = event;
	} else if(window.event) {
		eventObject = window.event;	
	} 
	var flyer = document.getElementById('flyer');
	flyer.style.top = (eventObject.clientY+window.pageYOffset-this.difY)+'px';
	flyer.style.left = (eventObject.clientX+window.pageXOffset-this.difX)+'px';
},
		
releaseDragger : function(){
	var header = document.getElementById('flyer_header');
	var flyer = document.getElementById('flyer');
	this.difX = 0;
	this.difY = 0;
	header.onmousemove='';
	flyer.onmousemove='';
}

}
