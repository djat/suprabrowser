package ss.framework.networking2;

public class CriticalCommandHandleException extends CommandHandleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -992163753123200251L;

	/**
	 * @param message
	 */
	public CriticalCommandHandleException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CriticalCommandHandleException(Throwable cause) {
		super(cause);
	}
	

}
