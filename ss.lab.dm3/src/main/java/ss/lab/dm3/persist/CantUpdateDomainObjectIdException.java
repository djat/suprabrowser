package ss.lab.dm3.persist;

/**
 * @author Dmitry Goncharov
 */
public class CantUpdateDomainObjectIdException extends DomainException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1981453977918990786L;


	/**
	 * @param domainObject
	 */
	public CantUpdateDomainObjectIdException(DomainObject domainObject) {
		super( "Can't update id of " + domainObject );
	}
}
