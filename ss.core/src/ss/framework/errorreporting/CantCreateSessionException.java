/**
 * 
 */
package ss.framework.errorreporting;


/**
 *
 */
public class CantCreateSessionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6024757381279754845L;

	/**
	 * @param createSessionInfo 
	 * @param cause
	 */
	public CantCreateSessionException(ICreateSessionInformation createSessionInfo, Throwable cause) {
		super( "Can't create user session by " + createSessionInfo, cause);
	}

	
}
