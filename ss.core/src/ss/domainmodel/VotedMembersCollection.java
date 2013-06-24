/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.xmlentities.IXmlEntityObjectFindCondition;
import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author roman
 *
 */
public class VotedMembersCollection extends XmlListEntityObject<VotedMember> {

	/**
	 * @param itemType
	 * @param itemName
	 */
	public VotedMembersCollection(Class<VotedMember> itemType, String itemName) {
		super(itemType, itemName);
	}
	
	public VotedMembersCollection(){
		super(VotedMember.class, VotedMember.ITEM_ROOT_ELEMENT_NAME);
	}
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( VotedMember item ) {
		VotedMember existedMember = getByContactName( item.getName() );
		if ( existedMember != null ) {
			remove( existedMember );
		}
		super.internalAdd(item);
	}
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(VotedMember entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public VotedMember get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(VotedMember fav: this) {
			this.remove(fav);
		}
	}
	
	/**
	 * @param loginName
	 */
	public VotedMember getByContactName(final String name) {
		return findFirst( new IXmlEntityObjectFindCondition<VotedMember>() {
			public boolean macth(VotedMember entityObject) {
				return entityObject.getName().equals(name);
			}			
		});
	}

	/**
	 * 
	 */
	public void clear() {
		for(VotedMember member : this) {
			remove(member);
		}
	}

}
