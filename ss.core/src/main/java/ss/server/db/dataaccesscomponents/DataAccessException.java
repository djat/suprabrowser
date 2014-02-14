/**
 * 
 */
package ss.server.db.dataaccesscomponents;

/**
 * @author d!ma
 *
 */
public class DataAccessException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5752770713879284369L;

	/**
	 * 
	 */
	public DataAccessException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DataAccessException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DataAccessException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}
