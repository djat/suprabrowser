package ss.lab.dm3.persist;

/**
 * @author Dmitry Goncharov
 */
public class ObjectIsPendingException extends DomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2782437387708939659L;

	/**
	 * @param domainObject
	 */
	public ObjectIsPendingException(DomainObject domainObject) {
		super( "Object " + domainObject + " was edited in previous transaction that is waiting acception from server." );
	}

}
