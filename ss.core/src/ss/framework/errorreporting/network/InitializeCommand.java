/**
 * 
 */
package ss.framework.errorreporting.network;

import ss.framework.errorreporting.ICreateSessionInformation;
import ss.framework.networking2.Command;

/**
 *
 */
public class InitializeCommand extends Command implements ICreateSessionInformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3799758009502346516L;

	private final String sessionKey;
	
	private final String userName;
	
	private final String context;

	/**
	 * @param sessionKey
	 * @param userName
	 * @param context
	 */
	public InitializeCommand(final String sessionKey, final String userName, final String context) {
		super();
		this.sessionKey = sessionKey;
		this.userName = userName;
		this.context = context;
	}

	/**
	 * @return the session key
	 */
	public String getSessionKey() {
		return this.sessionKey;
	}

	/**
	 * @return the user name
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @return the context
	 */
	public String getContext() {
		return this.context;
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.Message#toString()
	 */
	@Override
	public String toString() {
		return super.toString()
		+ " initialize for "
		+ ", sessionKey : " + this.sessionKey 
		+ ", userName: " + this.userName 
		+ ", context: " + this.context;
	}
	
	
	
	 
}
