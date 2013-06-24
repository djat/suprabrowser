/**
 * 
 */
package ss.client.networking.protocol.getters;

import java.util.Vector;

import ss.common.StringUtils;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetSpheresByRoleCommand extends AbstractGetterCommand {
	
	private static final long serialVersionUID = -9034481850432413863L;

	public void setSphereRoleList(final Vector<String> roles) {
		putArg(SessionConstants.SPHERE_TYPE, roles);
	}
	
	public Vector<String> getSphereRoleList() {
		return (Vector<String>)getObjectArg(SessionConstants.SPHERE_TYPE);
	}
	
	public void setContactName(final String name) {
		putArg(SessionConstants.CONTACT_NAME, name);
	}
	
	public String getContactName() {
		return getStringArg(SessionConstants.CONTACT_NAME);
	}
	
	public void setKeyword(final String keyword) {
		if(StringUtils.isBlank(keyword)) {
			return;
		}
		putArg(SessionConstants.KEYWORD_ELEMENT, keyword);
	}
	
	public String getKeyword() {
		return getStringArg(SessionConstants.KEYWORD_ELEMENT);
	}
}
