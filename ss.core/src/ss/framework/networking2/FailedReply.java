/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public class FailedReply extends Reply {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5324243390178778981L;
	
	private final String message;

	/**
	 * @param message
	 */
	public FailedReply(String message) {
		super();
		this.message = message;
	}
	
	/**
	 * @param message
	 */
	public FailedReply(Throwable cause ) {
		this( "Exception occured.", cause );  
	}
	
	/**
	 * @param message
	 */
	public FailedReply(String message, Throwable cause ) {
//		TODO: may be not toString?
		this( message + "Cause: " + ( cause != null ? " " + cause.toString() : "") );
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	@Override
	public String toString() {
		return "Reply failed. Details: " + getMessage();
	}
	
}
