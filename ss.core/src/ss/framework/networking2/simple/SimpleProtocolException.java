/**
 * 
 */
package ss.framework.networking2.simple;

/**
 *
 */
public class SimpleProtocolException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8316626376434797108L;

	/**
	 * @param message
	 * @param cause
	 */
	public SimpleProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SimpleProtocolException(String message) {
		super( message );
	}
	
	
}
