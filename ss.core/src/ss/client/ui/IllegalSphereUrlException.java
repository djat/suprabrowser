/**
 * 
 */
package ss.client.ui;

/**
 *
 */
public class IllegalSphereUrlException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6129604278179911144L;

	/**
	 * @param sphereUrl
	 * @param string
	 */
	public IllegalSphereUrlException(String sphereUrl, String details ) {
		super( "Can't parse sphere url: \"" + sphereUrl + "\". Details: " + details );
	}
}
