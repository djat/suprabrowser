package ss.common;

public class UnexpectedRuntimeException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 63740136985465718L;

	/**
	 * @param message
	 */
	public UnexpectedRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnexpectedRuntimeException(Exception cause) {
		super(cause);
	}

}
