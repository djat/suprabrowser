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
public class CommonEmailForwardingPreferences extends XmlEntityObject {
	
	public enum ForwardingModes {
		FORCED, AUTOMATIC, OFF
	}
	
	private final ISimpleEntityProperty emails = super
		.createAttributeProperty("forwarding/emails/@value");

	private final ISimpleEntityProperty mode = super
		.createAttributeProperty("forwarding/mode/@value");

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

	/**
	 * @return the mode
	 */
	public ForwardingModes getMode() {
		return this.mode.getEnumValue(ForwardingModes.class, ForwardingModes.OFF);
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(ForwardingModes mode) {
		this.mode.setEnumValue(mode);
	}
}
