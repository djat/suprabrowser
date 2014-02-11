package ss.lab.dm3.orm.managed;

import ss.lab.dm3.orm.MappedObject;

public interface IManagedCollection {

	ManagedCollectionController getController();
	
	void setUpController(MappedObject owner); 
	
}
