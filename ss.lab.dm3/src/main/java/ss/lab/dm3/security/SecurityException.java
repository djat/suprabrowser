package ss.lab.dm3.security;

public class SecurityException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4980197753965115764L;

	/**
	 * 
	 */
	public SecurityException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SecurityException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SecurityException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SecurityException(Throwable cause) {
		super(cause);
	}

	
}
