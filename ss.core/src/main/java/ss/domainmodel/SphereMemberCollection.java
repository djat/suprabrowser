package ss.domainmodel;

import java.util.ArrayList;
import java.util.List;

import ss.framework.entities.xmlentities.XmlListEntityObject;

public class SphereMemberCollection extends XmlListEntityObject<SphereMember>{
	
	public SphereMemberCollection() {
		super( SphereMember.class, SphereMember.ITEM_ROOT_ELEMENT_NAME);
	}
	
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( SphereMember item ) {
		super.internalAdd(item);
	}
	
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(SphereMember entity) {
		super.internalRemove(entity);
	}
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public SphereMember get(int index) {
		return super.internalGet(index);
	}
	
	public List<SphereMember> toList() {
		List<SphereMember> memberList = new ArrayList<SphereMember>();
		for(SphereMember member : this) {
			memberList.add(member);
		}
		return memberList;
	}

	/**
	 * @param contactNameByFirstAndLastNames
	 * @return
	 */
	public boolean contains(String contactNameByFirstAndLastNames) {
		if(contactNameByFirstAndLastNames==null) {
			return false;
		}
		for(SphereMember member : this) {
			if(member.getContactName().equals(contactNameByFirstAndLastNames)) {
				return true;
			}
		}
		return false;
	}
	
}
