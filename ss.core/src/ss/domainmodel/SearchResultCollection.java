/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author roman
 *
 */
public class SearchResultCollection extends XmlListEntityObject<SearchResultObject> {

	public SearchResultCollection(){
		super(SearchResultObject.class, SearchResultObject.ITEM_ROOT_ELEMENT_NAME);
	}
	
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( SearchResultObject item ) {
		super.internalAdd(item);
	}
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(SearchResultObject entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public SearchResultObject get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(SearchResultObject fav: this) {
			this.remove(fav);
		}
	}
}
