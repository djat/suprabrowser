/**
 * 
 */
package ss.client.ui.email;

/**
 * @author zobo
 *
 */
public class SupraEmailException extends Exception {

	/**
	 * 
	 */
	private static final String COMMON_EMAIL_EXCEPTION = "Common email exception";
	private static final long serialVersionUID = -6751506584999438160L;

	public SupraEmailException() {
		super();
	}

	public SupraEmailException(String message, Throwable cause) {
		super(message, cause);
	}

	public SupraEmailException(String message) {
		super(message);
	}

	public SupraEmailException(Throwable cause) {
		super(COMMON_EMAIL_EXCEPTION,cause);
	}

}
