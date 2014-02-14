/**
 * 
 */
package ss.common.domain.model.suprasphere;

import ss.common.domain.model.DomainObject;

/**
 * @author roman
 *
 */
public class MemberReferenceObject extends DomainObject {

	private String contactName;
	
	private String loginName;

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return this.contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return this.loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	
}
