/**
 * 
 */
package ss.server.domainmodel2.db.statements;

/**
 *
 */
public class CannotSetUpConnection extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3221658869217795291L;

	/**
	 * @param ex
	 */
	public CannotSetUpConnection(Throwable cause) {
		super( "Cannot set up connection", cause );
	}
}
