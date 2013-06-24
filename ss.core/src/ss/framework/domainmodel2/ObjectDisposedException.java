/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class ObjectDisposedException extends IllegalStateException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4523144193585675647L;

	/**
	 * @param transaction
	 */
	public ObjectDisposedException(Object object) {
		super( "Object disposed " + object );
	}

}
