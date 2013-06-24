/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author roman
 *
 */
public class FavouritesCollection extends XmlListEntityObject<FavouriteSphere> {

	public FavouritesCollection(){
		super(FavouriteSphere.class, FavouriteSphere.ITEM_ROOT_ELEMENT_NAME);
	}
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( FavouriteSphere item ) {
		super.internalAdd(item);
	}
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(FavouriteSphere entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public FavouriteSphere get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(FavouriteSphere fav: this) {
			this.remove(fav);
		}
	}
}
