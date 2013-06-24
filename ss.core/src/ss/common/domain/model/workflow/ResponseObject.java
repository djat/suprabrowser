/**
 * 
 */
package ss.common.domain.model.workflow;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.enums.Response;

/**
 * @author roman
 *
 */
public class ResponseObject extends DomainObject {

	private String loginName;
	
	private String contactName;
	
	private Response value;
	
	private long id;

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
	 * @return the value
	 */
	public Response getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Response value) {
		this.value = value;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
}
