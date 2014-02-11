/**
 * 
 */
package ss.lab.dm3.persist;

/**
 * @author Dmitry Goncharov
 */
public class ObjectNotInTransactionException extends DomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1750067523241267687L;

	/**
	 * @param repository
	 */
	public ObjectNotInTransactionException(Object object ) {
		super( "Object " + object + " not in transaction " );
	}
}
