/**
 * 
 */
package ss.framework.exceptions;


/**
 *
 */
public class ObjectIsNotInitializedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8263165482021559570L;

	/**
	 * 
	 */
	public ObjectIsNotInitializedException( Object obj ) {
		super( "Object is not initialized: " + obj );
	}

	/**
	 */
	public ObjectIsNotInitializedException(Object obj, String message ) {
		super( "Object is not initialized: " + obj + ". Details: " + message );
	}


}
