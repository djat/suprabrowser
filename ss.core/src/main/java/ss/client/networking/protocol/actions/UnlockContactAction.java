/**
 * 
 */
package ss.client.networking.protocol.actions;

import ss.common.StringUtils;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class UnlockContactAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnlockContactAction(final String username) {
		if(StringUtils.isBlank(username)) {
			throw new NullPointerException();
		}
		setLogin(username);
	}
	
	private void setLogin(final String username) {
		putArg(SessionConstants.USERNAME, username);
	}
	
	public String getLogin() {
		return getStringArg(SessionConstants.USERNAME);
	}

}
