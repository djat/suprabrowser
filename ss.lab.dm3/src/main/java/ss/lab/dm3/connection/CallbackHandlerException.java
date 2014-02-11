package ss.lab.dm3.connection;

/**
 * @author Dmitry Goncharov
 */
public class CallbackHandlerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5906330425252007775L;

	/**
	 * 
	 */
	public CallbackHandlerException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CallbackHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CallbackHandlerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CallbackHandlerException(Throwable cause) {
		super(cause);
	}

	
}
