/**
 * 
 */
package ss.domainmodel.systemstatemessage;

import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author roman
 *
 */
public class SystemPropertiesCollection extends XmlListEntityObject<SystemPropertyObject> {

	public SystemPropertiesCollection(){
		super(SystemPropertyObject.class, SystemPropertyObject.ITEM_ROOT_ELEMENT_NAME);
	}
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( SystemPropertyObject item ) {
		super.internalAdd(item);
	}
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(SystemPropertyObject entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public SystemPropertyObject get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(SystemPropertyObject fav: this) {
			this.remove(fav);
		}
	}
}
