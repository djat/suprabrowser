package ss.framework.entities.xmlentities;

import java.util.EventListener;

public interface IXmlListEntityListener extends EventListener {

	void itemInserted();
	
	void itemRemoved();	
	
}
