/**
 * 
 */
package ss.client.networking.protocol.getters;

import ss.common.StringUtils;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class IsContactLockedCommand extends AbstractGetterCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IsContactLockedCommand(final String username) {
		if(StringUtils.isBlank(username)) {
			throw new NullPointerException();
		}
		setLogin(username);
	}
	
	private void setLogin(final String username) {
		putArg(SessionConstants.LOGIN, username);
	}
	
	public String getLogin() {
		return getStringArg(SessionConstants.LOGIN);
	}

}
