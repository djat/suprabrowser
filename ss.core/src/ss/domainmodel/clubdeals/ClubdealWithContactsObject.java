/**
 * 
 */
package ss.domainmodel.clubdeals;

import java.util.ArrayList;
import java.util.List;

import ss.domainmodel.ContactStatement;

/**
 * @author roman
 *
 */
public class ClubdealWithContactsObject {

	private final ClubDeal clubdeal;
	
	private final List<ContactStatement> contacts = new ArrayList<ContactStatement>();
	
	public ClubdealWithContactsObject(final ClubDeal clubdeal) {
		this.clubdeal = clubdeal;
	}

	/**
	 * @return the clubdeal
	 */
	public ClubDeal getClubdeal() {
		return this.clubdeal;
	}

	/**
	 * @return the contacts
	 */
	public List<ContactStatement> getContacts() {
		return this.contacts;
	}
	
	public boolean hasContact(final String contactName) {
		if (contactName == null) {
			return false;
		}
		for(ContactStatement contact : getContacts()) {
			if(contact.getContactNameByFirstAndLastNames().equals(contactName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasContact(final ContactStatement contact) {
		return hasContact(contact.getContactNameByFirstAndLastNames());
	}
	
	public boolean addContact(final ContactStatement contact) {
		if(hasContact(contact)) {
			return false;
		}
		this.contacts.add(contact);
		return true;
	}
	 
	public ContactStatement getContactByName(final String contactName) {
		for(ContactStatement contact : getContacts()) {
			if(contact.getContactNameByFirstAndLastNames().equals(contactName)) {
				return contact;
			}
		}
		return null;
	}
	
	public boolean removeContact(final String contactName) {
		ContactStatement contactToRemove = getContactByName(contactName);
		if(contactToRemove == null) {
			return false;
		}
		this.contacts.remove(contactToRemove);
		return true;
	}
	
	public boolean removeContact(final ContactStatement contact) {
		return removeContact(contact.getContactNameByFirstAndLastNames());
	}

	/**
	 * @return
	 */
	public String getClubdealSystemName() {
		return getClubdeal().getSystemName();
	}
	
	public String getClubDealDisplayName() {
		return getClubdeal().getDisplayName();
	}
}
