//netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");

var responseId = '3287962947845429920';

var myObserver = { observe : function(subject, topic, data)  {
                                      if (topic == "myTopic") {
						alert('holy fucking shit mother of god');
                                      }
					else {

					alert('asdf');

					}
                                   }
                               };


 var observerService =
 Components.classes["@mozilla.org/observer-service;1"].

 getService(Components.interfaces.nsIObserverService);
   observerService.addObserver(myObserver, "myTopic", false);
   
   function testJS()
{
	//alert("David Thompson: Its easy to call JS functions and we can do wonders!");
    alert(actSubject);
    
}

	function executePopup(id) {
	    
	    var elem = document.getElementById(id);
		//elem.parentNode.removeChild(elem);
		drag_object.initialize();
	
		scroller_object.init (elem,id,0);
		
		
	}
