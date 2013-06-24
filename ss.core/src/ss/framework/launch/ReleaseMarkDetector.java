/**
 * 
 */
package ss.framework.launch;


/**
 *
 */
final class ReleaseMarkDetector implements IReleaseMarkDetector {

	/**
	 * 
	 */
	static final String INITIALIZATION_MARK = "--- SS APPLICATION INITIALIZED ---";

	/* (non-Javadoc)
	 * @see ss.framework.launch.IReleaseMarkDetector#match(java.lang.String)
	 */
	public boolean match( String line ) {
		return line.contains( INITIALIZATION_MARK );
	}
	
	
}
