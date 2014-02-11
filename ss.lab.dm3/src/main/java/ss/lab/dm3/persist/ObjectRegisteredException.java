package ss.lab.dm3.persist;

/**
 * @author Dmitry Goncharov
 */
public class ObjectRegisteredException extends DomainException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4091155714556219113L;

	/**
	 * @param registered
	 * @param otherObject
	 */
	public ObjectRegisteredException(DomainObject registered,
			DomainObject otherObject) {
		super( "Found registered object " + registered
		+ " by " + otherObject );
	}

	/**
	 * @param domainObject
	 */
	public ObjectRegisteredException(DomainObject domainObject) {
		super( "Object with same id already registered. Other object is " + domainObject );
	}

	
}
