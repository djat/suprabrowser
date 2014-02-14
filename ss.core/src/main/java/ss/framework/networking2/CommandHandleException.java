package ss.framework.networking2;

public class CommandHandleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3801357505568324838L;

	
	/**
	 * @param message
	 * @param cause
	 */
	public CommandHandleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CommandHandleException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CommandHandleException(Throwable cause) {
		super(cause);
	}

	
}
