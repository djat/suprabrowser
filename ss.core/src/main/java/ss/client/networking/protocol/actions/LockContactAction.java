/**
 * 
 */
package ss.client.networking.protocol.actions;

import ss.util.SessionConstants;
import ss.common.StringUtils;

/**
 * @author roman
 *
 */
public class LockContactAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LockContactAction(final String username) {
		if(StringUtils.isBlank(username)) {
			throw new NullPointerException();
		}
		setLogin(username);
	}

	/**
	 * @return
	 */
	public String getLogin() {
		return getStringArg(SessionConstants.USERNAME);
	}
	
	private void setLogin(final String username) {
		putArg(SessionConstants.USERNAME, username);
	}

}
