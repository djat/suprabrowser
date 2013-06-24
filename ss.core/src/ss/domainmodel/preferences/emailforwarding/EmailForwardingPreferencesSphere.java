/**
 * 
 */
package ss.domainmodel.preferences.emailforwarding;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author zobo
 *
 */
public class EmailForwardingPreferencesSphere extends XmlEntityObject{
	
	
	public enum SphereForwardingModes {
		CONTACTS, MEMBERS, ADDITIONAL, OFF
	}
	
	private final ISimpleEntityProperty mode = super
		.createAttributeProperty("forwarding/mode/@value");
	
	/**
	 * Additional to contacts/members emails
	 */
	private final ISimpleEntityProperty emails = super
		.createAttributeProperty("forwarding/emails/@value");
	
	private final ISimpleEntityProperty additional = super
	.createAttributeProperty("forwarding/additional/@value");
	
	/**
	 * @return the mode
	 */
	public SphereForwardingModes getMode() {
		return this.mode.getEnumValue(SphereForwardingModes.class, SphereForwardingModes.OFF);
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(SphereForwardingModes mode) {
		this.mode.setEnumValue(mode);
	}
	
	/**
	 * @return the emails
	 */
	public String getEmails() {
		return this.emails.getValueOrEmpty();
	}

	/**
	 * @param emails the emails to set
	 */
	public void setEmails(String emails) {
		this.emails.setValue(emails);
	}

	public boolean getAdditional() {
		return this.additional.getBooleanValue(false);
	}

	public void setAdditional(boolean value){
		this.additional.setBooleanValue(value);
	}
}
