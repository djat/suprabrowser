package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;


public class UserStatementCollection extends XmlListEntityObject<UserStatement>{

	public UserStatementCollection() {
		super( UserStatement.class, UserStatement.ITEM_ROOT_ELEMENT_NAME);
	}
	
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( UserStatement item ) {
		super.internalAdd(item);
	}

	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(UserStatement entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public UserStatement get(int index) {
		return super.internalGet(index);
	}
}
