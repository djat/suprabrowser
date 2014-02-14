/**
 * 
 */
package ss.framework.launch;

/**
 *
 */
public class CantLaunchJarException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3040978658457204988L;

	/**
	 * @param message
	 * @param cause
	 */
	public CantLaunchJarException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CantLaunchJarException(String message) {
		super(message);
	}


	
}
