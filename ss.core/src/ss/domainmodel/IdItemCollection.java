/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author roman
 *
 */
public class IdItemCollection extends XmlListEntityObject<IdItem> {

	public IdItemCollection(){
		super(IdItem.class, IdItem.ITEM_ROOT_ELEMENT_NAME);
	}
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( IdItem item ) {
		super.internalAdd(item);
	}
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(IdItem entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public IdItem get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(IdItem fav: this) {
			this.remove(fav);
		}
	}
}
