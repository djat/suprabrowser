/**
 * 
 */
package ss.framework.install.update;

/**
 *
 */
public class CantUpdateApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2754566777220297807L;

	/**
	 * @param message
	 * @param cause
	 */
	public CantUpdateApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CantUpdateApplicationException(String message) {
		super(message);
	}

	
}
