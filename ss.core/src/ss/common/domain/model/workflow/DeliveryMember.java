/**
 * 
 */
package ss.common.domain.model.workflow;

import ss.common.domain.model.enums.Role;
import ss.common.domain.model.suprasphere.MemberReferenceObject;

/**
 * @author roman
 *
 */
public class DeliveryMember extends MemberReferenceObject {

	private Role role;

	/**
	 * @return the role
	 */
	public Role getRole() {
		return this.role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}
}
