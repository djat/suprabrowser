/**
 * 
 */
package ss.framework.threads;


/**
 *
 */
public class CantInitializeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6738440886289365904L;

	/**
	 * @param cause
	 */
	public CantInitializeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param string
	 */
	public CantInitializeException(String message) {
		super( message );
	}
	
}
