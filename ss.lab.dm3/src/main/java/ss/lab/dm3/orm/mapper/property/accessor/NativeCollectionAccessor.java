/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.accessor;

import java.util.Collection;
import java.util.List;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.IAccessor;

/**
 *
 * @author Dmitry Goncharov
 */
public class NativeCollectionAccessor extends CollectionAccessor {

	/**
	 * @param collectionPropertyAccessor
	 * @param mappedByName
	 * @param itemType
	 */
	public NativeCollectionAccessor(IAccessor collectionPropertyAccessor, String mappedByName,
			Class<? extends MappedObject> itemType) {
		super(collectionPropertyAccessor, mappedByName, itemType);
	}

	@SuppressWarnings("unchecked")
	private Collection<MappedObject> getCollection(MappedObject bean) {
		return (Collection<MappedObject>) getValue(bean);
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.managed.ICollectionAccessor#addByReference(ss.lab.dm3.orm.MappedObject, ss.lab.dm3.orm.MappedObject)
	 */
	public void addByOrm(MappedObject collectionOwnerBean,
			MappedObject item) {
		Collection<MappedObject> collection = getCollection(collectionOwnerBean);
		collection.add( item );		
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.managed.ICollectionAccessor#removeByReference(ss.lab.dm3.orm.MappedObject, ss.lab.dm3.orm.MappedObject)
	 */
	public void removeByOrm(MappedObject collectionOwnerBean,
			MappedObject item) {
		Collection<MappedObject> collection = getCollection(collectionOwnerBean);
		collection.remove( item );
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.managed.ICollectionAccessor#setUpByOrm(ss.lab.dm3.orm.MappedObject, java.util.List)
	 */
	public void setUpByOrm(MappedObject collectionOwnerBean,
			List<? extends MappedObject> items) {
		// TODG Auto-generated method stub
		
	}

}
