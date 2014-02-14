/**
 * 
 */
package ss.common.domain.model.preferences;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.enums.SphereForwardingModes;

/**
 * @author roman
 *
 */
public class EmailForwardingPreferencesSphere extends DomainObject {

	private boolean additional;
	
	private String emails;
	
	private SphereForwardingModes forwardingMode;

	/**
	 * @return the additional
	 */
	public boolean isAdditional() {
		return this.additional;
	}

	/**
	 * @param additional the additional to set
	 */
	public void setAdditional(boolean additional) {
		this.additional = additional;
	}

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
	 * @return the forwardingMode
	 */
	public SphereForwardingModes getForwardingMode() {
		return this.forwardingMode;
	}

	/**
	 * @param forwardingMode the forwardingMode to set
	 */
	public void setForwardingMode(SphereForwardingModes forwardingMode) {
		this.forwardingMode = forwardingMode;
	}
}
