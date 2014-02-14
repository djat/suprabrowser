/**
 * 
 */
package ss.common.domain.model.preferences;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.enums.ForwardingMode;

/**
 * @author roman
 *
 */
public class CommonEmailPreferences extends DomainObject {

	private String emails;
	
	private ForwardingMode mode;

	/**
	 * @return the emails
	 */
	public String getEmails() {
		return this.emails;
	}

	/**
	 * @param emails the emails to set
	 */
	public void setEmails(String emails) {
		this.emails = emails;
	}

	/**
	 * @return the mode
	 */
	public ForwardingMode getMode() {
		return this.mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(ForwardingMode mode) {
		this.mode = mode;
	}	
}
