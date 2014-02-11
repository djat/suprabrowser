/**
 * 
 */
package ss.lab.dm3.orm.managed;

import java.util.List;

import ss.lab.dm3.orm.MappedObject;

/**
 * @author Dmitry Goncharov
 */
public interface ICollectionAccessor {

	String getMappedByName();

	Class<? extends MappedObject> getItemType();
	
	void setUpByOrm(MappedObject collectionOwnerBean,
			List<? extends MappedObject> items);
	
	void addByOrm(MappedObject collectionOwnerBean, MappedObject item);
	
	void removeByOrm(MappedObject collectionOwnerBean, MappedObject item);
	
}
