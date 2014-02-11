package ss.lab.dm3.security2.backend.storage.jdbc;

public class SecurityDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1245052006839642692L;

	/**
	 * 
	 */
	public SecurityDataException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SecurityDataException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SecurityDataException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SecurityDataException(Throwable cause) {
		super(cause);
	}

	
}

