/**
 * 
 */
package ss.client.networking.protocol.actions;

import ss.domainmodel.configuration.SphereRoleObject;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class SphereRoleRenameAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void putSphereRole(final SphereRoleObject roleObject) {
		putArg(SessionConstants.SPHERE_TYPE, roleObject.getRoleName());
	}
	
	public String getSphereRole() {
		return getStringArg(SessionConstants.SPHERE_TYPE);
	}

	public void putReplacement(final String replacement) {
		putArg(SessionConstants.REPLACEMENT, replacement);
	}
	
	public String getReplacement() {
		return getStringArg(SessionConstants.REPLACEMENT);
	}
}
