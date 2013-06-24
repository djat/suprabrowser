/**
 * 
 */
package ss.common.domain.model.clubdeals;

import java.util.Collection;
import java.util.TreeSet;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.collections.AssociatedFileCollection;
import ss.common.domain.model.collections.ClubDealContactCollection;
import ss.common.domain.model.collections.TypeCollection;

/**
 * @author roman
 * 
 */
public class ClubDealObject extends DomainObject {

	private String clubdealId;

	private String name;

	private final AssociatedFileCollection files = new AssociatedFileCollection();

	private final ClubDealContactCollection contacts = new ClubDealContactCollection();

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the clubdealId
	 */
	public String getClubdealId() {
		return this.clubdealId;
	}

	/**
	 * @param clubdealId
	 *            the clubdealId to set
	 */
	public void setClubdealId(String clubdealId) {
		this.clubdealId = clubdealId;
	}

	/**
	 * @return the files
	 */
	public AssociatedFileCollection getFiles() {
		return this.files;
	}

	/**
	 * @return the contacts
	 */
	public ClubDealContactCollection getContacts() {
		return this.contacts;
	}

	/**
	 * @param contactName
	 * @return
	 */
	public boolean hasContact(final String contactName) {
		for (ClubDealContactObject contact : this.contacts) {
			if (contact.getContactName().equals(contactName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public Collection<TypeRecord> getContactTypes() {
		Collection<TypeRecord> types = new TreeSet<TypeRecord>();
		for (ClubDealContactObject contact : this.contacts) {
			types.add(contact.getType());
		}
		return types;
	}

	public ClubDealContactCollection getClubDealContacts(
			final ClubDealObject cd, final TypeCollection types) {
		ClubDealContactCollection contacts = new ClubDealContactCollection();
		for (ClubDealContactObject contact : this.contacts) {
			if (types.contains(contact.getType())) {
				contacts.add(contact);
			}
		}
		return contacts;
	}

	/**
	 * @param contactName
	 */
	public void addContact(String contactName) {
		ClubDealContactObject contact = new ClubDealContactObject();
		contact.setContactName(contactName);
		getContacts().add(contact);
	}

	/**
	 * @param contactName
	 */
	public void removeContact(final String contactName) {
		if(contactName==null) {
			return;
		}
		ClubDealContactObject contact = getContacts().getContactByName(contactName);
		if(contact!=null) {
			getContacts().remove(contact);
		}
	}

	/**
	 * @param contactName
	 * @param type
	 */
	public void setTypeToContact(String contactName, TypeRecord type) {
		ClubDealContactObject contact = getContacts().getContactByName(contactName);
		if(contact==null) {
			return;
		}
		contact.setType(type);
	}

	/**
	 * @param type
	 */
	public void typeRemoved(final TypeRecord type) {
		for(ClubDealContactObject contact : getContacts()) {
			if(contact.getType().equals(type)) {
				contact.setType(TypeRecord.createEmptyType());
			}
		}
	}

	/**
	 * @param contactName
	 * @return
	 */
	public ClubDealContactObject getContactByName(final String contactName) {
		return getContacts().getContactByName(contactName);
	}
}
