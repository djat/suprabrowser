/**
 * 
 */
package ss.server.domainmodel2.db;


/**
 *
 */
public class ResultSetRowHandlerException extends Exception {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3267339817083930902L;

	/**
	 * @param ex
	 */
	public ResultSetRowHandlerException(Throwable cause ) {
		super( cause );
	}

	/**
	 * @return
	 */
	public Throwable getRealException() {
		return getCause();
	}
}
