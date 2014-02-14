/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class CannotMarkObjectAnsNewException extends IllegalArgumentException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2606501134984780269L;

	/**
	 * @param object
	 */
	public CannotMarkObjectAnsNewException(DomainObject object) {
		super( "Cannot mark object as new " + object );
	}


}
