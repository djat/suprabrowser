/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author roman
 *
 */
public class RoleMemberCollection extends XmlListEntityObject<RoleMemberItem>{


	public RoleMemberCollection(){
		super(RoleMemberItem.class, RoleMemberItem.ITEM_ROOT_ELEMENT_NAME);
	}
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( RoleMemberItem item ) {
		super.internalAdd(item);
	}
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(RoleMemberItem entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public RoleMemberItem get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(RoleMemberItem fav: this) {
			this.remove(fav);
		}
	}

}
