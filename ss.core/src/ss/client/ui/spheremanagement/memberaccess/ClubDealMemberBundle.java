/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import ss.domainmodel.ContactStatement;

/**
 * @author roman
 *
 */
public class ClubDealMemberBundle {
	
	private final String clubdealId;
	
	private final ContactStatement contact;
	
	private String type;
	
	public ClubDealMemberBundle(final String clubdealId, final ContactStatement contact) {
		this( clubdealId, contact, null );
	}
	
	public ClubDealMemberBundle( final String clubdealId, final ContactStatement contact, final String type ) { 
		this.clubdealId = clubdealId;
		this.contact = contact;
		this.type = type;
	}

	/**
	 * @return the clubdeal
	 */
	public String getClubdealId() {
		return this.clubdealId;
	}

	/**
	 * @return the contact
	 */
	public ContactStatement getContact() {
		return this.contact;
	}
	
	/**
	 * @param sphereId
	 * @param contact
	 * @return
	 */
	public boolean isSame(String clubdealId, String contactName) {
		return this.clubdealId.equals(clubdealId)
				&& this.contact.getContactNameByFirstAndLastNames().equals(
						contactName);
	}

	public String getType() {
		return this.type;
	}

	/**
	 * equals by references of ClubDeal and ContactStatement, not by content
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClubDealMemberBundle)) {
			return false;
		}
		final ClubDealMemberBundle external = (ClubDealMemberBundle) obj;
		if (!external.getContact().getContactNameByFirstAndLastNames().equals(getContact().getContactNameByFirstAndLastNames())) {
			return false;
		}
		if (!external.getClubdealId().equals(getClubdealId())) {
			return false;
		}
		return true;
	}

	public void setType(String type) {
		this.type = type;
	}
}
