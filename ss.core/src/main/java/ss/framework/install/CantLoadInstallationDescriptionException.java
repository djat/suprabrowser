/**
 * 
 */
package ss.framework.install;

/**
 *
 */
public class CantLoadInstallationDescriptionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6136002790262977076L;

	/**
	 * @param message
	 */
	public CantLoadInstallationDescriptionException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CantLoadInstallationDescriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
