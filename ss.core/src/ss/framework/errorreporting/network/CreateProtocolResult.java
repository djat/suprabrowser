/**
 * 
 */
package ss.framework.errorreporting.network;

import ss.framework.networking2.Protocol;

/**
 *
 */
public class CreateProtocolResult {

	private final Protocol protocol;
	
	private final String sessionKey;
	
	private final String userLogin;
	
	
	/**
	 * @param protocol
	 * @param sessionKey
	 * @param userLogin
	 */
	public CreateProtocolResult(final Protocol protocol, final String sessionKey, final String userLogin) {
		super();
		this.protocol = protocol;
		this.sessionKey = sessionKey;
		this.userLogin = userLogin;
	}


	/**
	 * @return the user login
	 */
	public String getUserLogin() {
		return this.userLogin;
	}


	/**
	 * @return the sessionKey
	 */
	public String getSessionKey() {
		return this.sessionKey;
	}


	/**
	 * @return the protocol
	 */
	public Protocol getProtocol() {
		return this.protocol;
	}


}
