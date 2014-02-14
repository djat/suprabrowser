/**
 * 
 */
package ss.framework.install.update;



/**
 *
 */
public class CantCreateUpdateProtocolException extends CantUpdateApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -20983822292398442L;


	/**
	 * @param message
	 * @param ex
	 */
	public CantCreateUpdateProtocolException(String message, Throwable ex) {
		super( message, ex );
	}


}
