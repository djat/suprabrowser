package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;

public class SphereLocationCollection extends XmlListEntityObject<SphereLocation>  {

	public SphereLocationCollection() {
		super( SphereLocation.class, SphereLocation.ITEM_ROOT_ELEMENT_NAME);
	}
	
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( SphereLocation item ) {
		super.internalAdd(item);
	}

	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(SphereLocation entity) {
		super.internalRemove(entity);
	}
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public SphereLocation get(int index) {
		return super.internalGet(index);
	}
	


}

