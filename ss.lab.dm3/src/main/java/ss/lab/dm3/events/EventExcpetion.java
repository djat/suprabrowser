package ss.lab.dm3.events;

/**
 * @author Dmitry Goncharov
 */
public class EventExcpetion extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5904527197848867920L;

	/**
	 * 
	 */
	public EventExcpetion() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EventExcpetion(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public EventExcpetion(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public EventExcpetion(Throwable cause) {
		super(cause);
	}

	
}
