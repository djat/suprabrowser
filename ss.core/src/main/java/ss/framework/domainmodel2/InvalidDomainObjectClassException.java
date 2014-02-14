/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class InvalidDomainObjectClassException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1976896462427192492L;

	/**
	 * @param domainObjectClass
	 * @param class1
	 */
	public InvalidDomainObjectClassException(Class expected, Class actual) {
		super( "Invalid object class. Expected " + expected + ", actual " + actual );
	}

}
