/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author roman
 *
 */
public class RoleItemCollection extends XmlListEntityObject<RoleItem> {

	public RoleItemCollection(){
		super(RoleItem.class, RoleItem.ITEM_ROOT_ELEMENT_NAME);
	}
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( RoleItem item ) {
		super.internalAdd(item);
	}
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(RoleItem entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public RoleItem get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(RoleItem fav: this) {
			this.remove(fav);
		}
	}
}
