/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class ObjectNotInTransactionException extends IllegalStateException {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1363044050989684808L;

	/**
	 * @param object
	 */
	public ObjectNotInTransactionException(Object object) {
		super( "Object not in transaction " + object );
	}
}
