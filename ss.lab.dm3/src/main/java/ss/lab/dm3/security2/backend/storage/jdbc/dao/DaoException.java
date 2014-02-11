package ss.lab.dm3.security2.backend.storage.jdbc.dao;

public class DaoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2169171643610502464L;

	/**
	 * 
	 */
	public DaoException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DaoException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DaoException(Throwable cause) {
		super(cause);
	}

	
}
