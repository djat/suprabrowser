/**
 * 
 */
package ss.common.domain.model.suprasphere;

import ss.common.domain.model.DomainObject;

/**
 * @author roman
 *
 */
public class SphereEmailObject extends DomainObject {

	private String sphereId;
	
	private boolean enabled;
	
	private String emailName;
	
	private String messageIdAdd;

	/**
	 * @return the sphereId
	 */
	public String getSphereId() {
		return this.sphereId;
	}

	/**
	 * @param sphereId the sphereId to set
	 */
	public void setSphereId(String sphereId) {
		this.sphereId = sphereId;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the emailName
	 */
	public String getEmailName() {
		return this.emailName;
	}

	/**
	 * @param emailName the emailName to set
	 */
	public void setEmailName(String emailName) {
		this.emailName = emailName;
	}

	/**
	 * @return the messageIdAdd
	 */
	public String getMessageIdAdd() {
		return this.messageIdAdd;
	}

	/**
	 * @param messageIdAdd the messageIdAdd to set
	 */
	public void setMessageIdAdd(String messageIdAdd) {
		this.messageIdAdd = messageIdAdd;
	}
}
