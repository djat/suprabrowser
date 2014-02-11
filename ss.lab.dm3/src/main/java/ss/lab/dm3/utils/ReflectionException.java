package ss.lab.dm3.utils;

public class ReflectionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1883183099392599220L;

	/**
	 * 
	 */
	public ReflectionException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ReflectionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ReflectionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ReflectionException(Throwable cause) {
		super(cause);
	}

	
}
