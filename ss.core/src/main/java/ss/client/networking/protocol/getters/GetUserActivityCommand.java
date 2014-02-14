/**
 * 
 */
package ss.client.networking.protocol.getters;

import ss.server.networking.SC;

/**
 * 
 */
public class GetUserActivityCommand extends AbstractGetterCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1591644931886905002L;

	/**
	 * @param sphereId
	 * @param login
	 */
	public GetUserActivityCommand(final String sphereId, final String login) {
		super();
		putArg( SC.SPHERE_ID2, sphereId );
		putArg( SC.LOGIN_NAME, login );
	}

	/**
	 * @return the sphereId
	 */
	public String getSphereId() {
		return this.getStringArg( SC.SPHERE_ID2 );
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return this.getStringArg( SC.LOGIN_NAME );
	}

}
