/**
 * 
 */
package ss.framework.install.update;

/**
 *
 */
public class CantArrangeFileException extends CantUpdateApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8375151371767263328L;

	/**
	 * @param message
	 * @param cause
	 */
	public CantArrangeFileException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CantArrangeFileException(String message) {
		super(message);
	}

	
}
