/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class CannotAllowObjectFromAnotherTransactionException extends
		IllegalArgumentException {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3324888933621783636L;

	/**
	 * @param domainObject
	 */
	public CannotAllowObjectFromAnotherTransactionException(DomainObject domainObject) {
		super( "Cannot allow object from another transaction " + domainObject );
	}
}
