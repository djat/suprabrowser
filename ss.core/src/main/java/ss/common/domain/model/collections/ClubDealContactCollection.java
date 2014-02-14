/**
 * 
 */
package ss.common.domain.model.collections;

import ss.common.domain.model.clubdeals.ClubDealContactObject;

/**
 * @author roman
 *
 */
public class ClubDealContactCollection extends DomainObjectList<ClubDealContactObject> {

	@Override
	public boolean contains(final ClubDealContactObject item) {
		if(item==null) {
			return false;
		}
		for(ClubDealContactObject contact : this) {
			if(contact.getContactName().equals(item.getContactName())) {
				return true;
			}
		}
		return false;
	}
	
	public ClubDealContactObject getContactByName(final String contactName) {
		for(ClubDealContactObject contact : this) {
			if(contact.getContactName().equals(contactName)) {
				return contact;
			}
		}
		return null;
	}

	
}
