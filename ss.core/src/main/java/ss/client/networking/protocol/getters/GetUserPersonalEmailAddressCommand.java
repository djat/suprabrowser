/**
 * 
 */
package ss.client.networking.protocol.getters;

/**
 * @author zobo
 *
 */
public class GetUserPersonalEmailAddressCommand extends AbstractGetterCommand {

	private static final long serialVersionUID = -2137574398005101721L;

	private static final String LOGIN = "login_identifier";

	private static final String SPHERE_ID = "sphere_id";

	/**
	 * If not specified, private sphere will be used. 
	 */
	public void setSphereId( final String sphereId ){
		putArg( SPHERE_ID, sphereId );
	}
	
	public String getSphereId(){
		return getStringArg( SPHERE_ID );
	}
	
	/**
	 * If not specified current user will be used.
	 */
	public void setUserLogin( final String login ){
		putArg( LOGIN, login );
	}
	
	public String getUserLogin() {
		return getStringArg( LOGIN );
	}
}
