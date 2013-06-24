/**
 * 
 */
package ss.common.path;

/**
 *
 */
public class CantCreateClassLocationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8335311865216834982L;

	/**
	 * @param message
	 * @param cause
	 */
	public CantCreateClassLocationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CantCreateClassLocationException(String message) {
		super(message);
	}

}
