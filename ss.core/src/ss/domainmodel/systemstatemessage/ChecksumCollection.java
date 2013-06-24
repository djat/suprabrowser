/**
 * 
 */
package ss.domainmodel.systemstatemessage;

import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author roman
 *
 */
public class ChecksumCollection extends XmlListEntityObject<ChecksumItem> {

	public ChecksumCollection(){
		super(ChecksumItem.class, ChecksumItem.ITEM_ROOT_ELEMENT_NAME);
	}
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( ChecksumItem item ) {
		super.internalAdd(item);
	}
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(ChecksumItem entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public ChecksumItem get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(ChecksumItem fav: this) {
			this.remove(fav);
		}
	}
}
