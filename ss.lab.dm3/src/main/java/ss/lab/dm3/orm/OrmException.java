package ss.lab.dm3.orm;

public class OrmException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 284565810875044403L;

	/**
	 * 
	 */
	public OrmException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OrmException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public OrmException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OrmException(Throwable cause) {
		super(cause);
	}

	
}
