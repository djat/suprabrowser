/**
 * 
 */
package ss.domainmodel.clubdeals;

import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.SphereMember;
import ss.domainmodel.SphereStatement;
import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class ClubDeal extends SphereStatement {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = SSLogger.getLogger(ClubDeal.class);
	
	@SuppressWarnings("unchecked")
	public static ClubDeal wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, ClubDeal.class);
	}

	public long getId() {
		return Long.parseLong(getSystemName());
	}
	
	public String getStringId() {
		return getSystemName();
	}

	public void setId( final long id ) {
		setSystemName(new Long(id).toString());
	}
	
	public void setId( final String id ) {
		setSystemName(id);
	}

	public String getName() {
		return getDisplayName();
	}

	public void setName( final String name ) {
		setDisplayName(name);
	}
	
	public boolean hasContact( final String contactName ){
		for(SphereMember member : getSphereMembers()) {
			if(member.getContactName().equals(contactName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param contactName
	 */
	public boolean removeContact(final String contactName) {
		SphereMember member = getMemberByContactName(contactName);
		if(member!=null) {
			this.getSphereMembers().remove(member);
			return true;
		}
		return false;
	}

	/**
	 * @param contactName
	 * @return
	 */
	public SphereMember getMemberByContactName(String contactName) {
		for(SphereMember member : getSphereMembers()) {
			if(member.getContactName().equals(contactName)) {
				return member;
			}
		}
		return null;
	}

	/**
	 * @param contactName
	 */
	public boolean addContact(final String contactName) {
		if(contactName==null) {
			return false;
		}
		if(hasContact(contactName)) {
			return false;
		}
		SphereMember newItem = new SphereMember();
		newItem.setContactName(contactName);
		newItem.setLoginName(SupraSphereFrame.INSTANCE.client.getVerifyAuth().getLoginForContact(contactName));
		newItem.setType(SphereMember.NO_TYPE);
		getSphereMembers().add(newItem);
		return true;
	}
	
	public void setTypeToContact(String contact, String type) {
		getMemberByContactName(contact).setType(type);
	}

	/**
	 * @param type
	 * @return
	 */
	public boolean removeType(String type) {
		boolean removed = false;
		for(SphereMember member : getSphereMembers()) {
			if(member.getType().equals(type)) {
				member.setType(SphereMember.NO_TYPE);
				removed = true;
			}
		}
		return removed;
	}

	/**
	 * @param selectedType
	 * @param newName
	 * @return
	 */
	public boolean renameType(String selectedType, String newName) {
		boolean renamed = false;
		for(SphereMember member : getSphereMembers()) {
			if(member.getType().equals(selectedType)) {
				member.setType(newName);
				renamed = true;
			}
		}
		return renamed;
	}
}
