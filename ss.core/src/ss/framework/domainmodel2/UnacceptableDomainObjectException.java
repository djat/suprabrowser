/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public class UnacceptableDomainObjectException extends IllegalArgumentException {



	/**
	 * 
	 */
	private static final long serialVersionUID = -5182774750008844115L;

	/**
	 * @param baseDomainObjectClass
	 * @param domainObject
	 */
	public UnacceptableDomainObjectException(Class baseDomainObjectClass, DomainObject domainObject) {
		super( "Unacceptable domain object. Base class " +  baseDomainObjectClass + " actual class " + domainObject );
	}

}
