package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;

public class WindowPositionCollection extends XmlListEntityObject<WindowPosition> {
	
	public WindowPositionCollection() {
		super(WindowPosition.class, WindowPosition.ITEM_ROOT_ELEMENT_NAME);
	}
	
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( WindowPosition item ) {
		super.internalAdd(item);
	}

	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(WindowPosition entity) {
		super.internalRemove(entity);
	}
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public WindowPosition get(int index) {
		return super.internalGet(index);
	}
}
