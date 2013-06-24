/**
 * 
 */
package ss.common.threads;

/**
 *
 */
public class ObjectRefusedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8273113193174277774L;
	
	/**
	 * @param name
	 * @param string
	 */
	public ObjectRefusedException(Object obj, String details) {
		super( "Object refused: " + obj + ". Details " + details );
	}
	
	/**
	 * @param ex
	 */
	public ObjectRefusedException(Object obj, InterruptedException ex) {
		super( "Object refused: " + obj, ex );
	}

}
