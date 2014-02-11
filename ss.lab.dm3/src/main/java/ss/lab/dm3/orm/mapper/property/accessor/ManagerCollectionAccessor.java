/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.accessor;

import java.util.List;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.managed.IManagedCollection;
import ss.lab.dm3.orm.managed.ManagedCollectionController;
import ss.lab.dm3.orm.mapper.property.IAccessor;

/**
 * @author Dmitry Goncharov
 */
public class ManagerCollectionAccessor extends CollectionAccessor implements IManagedAccessor {

	/**
	 * @param collectionPropertyAccessor
	 * @param mappedByName
	 * @param itemType
	 */
	public ManagerCollectionAccessor(IAccessor collectionPropertyAccessor, String mappedByName,
			Class<? extends MappedObject> itemType) {
		super(collectionPropertyAccessor, mappedByName, itemType);
	}

	public void setUpManagedFeatures(Object bean) {
		final IManagedCollection collection = (IManagedCollection) getValue(bean);
		collection.setUpController((MappedObject)bean);
		final ManagedCollectionController collectionController = collection.getController();
		collectionController.setMappedByName(getMappedByName());
		collectionController.setItemType(getItemType());
	}
	
	public void setUpByOrm(MappedObject collectionOwnerBean,
			List<? extends MappedObject> items) {
		ManagedCollectionController managedCollection = getCollectionController(collectionOwnerBean);
		managedCollection.setUpByOrm(items);
	}
	
	private ManagedCollectionController getCollectionController(Object bean) {
		final IManagedCollection collection = (IManagedCollection) getValue(bean);
		if ( collection == null ) {
			throw new NullPointerException( "collection property " + this + " is null in " + bean );
		}
		return collection.getController();
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.managed.ICollectionAccessor#addByReference(ss.lab.dm3.orm.MappedObject, ss.lab.dm3.orm.MappedObject)
	 */
	public void addByOrm(MappedObject collectionOwnerBean,
			MappedObject item) {
		ManagedCollectionController managedCollection = getCollectionController(collectionOwnerBean);
		managedCollection.addByOrm(item);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.managed.ICollectionAccessor#removeByReference(ss.lab.dm3.orm.MappedObject, ss.lab.dm3.orm.MappedObject)
	 */
	public void removeByOrm(MappedObject collectionOwnerBean,
			MappedObject item) {
		ManagedCollectionController managedCollection = getCollectionController(collectionOwnerBean);
		managedCollection.removeByOrm(item);
	}


	@Override
	public void resetToDefault(Object bean) {
		super.resetToDefault(bean);
		final ManagedCollectionController collection = getCollectionController( bean);
		collection.resetFetchedItems();
	}
	
	

}
