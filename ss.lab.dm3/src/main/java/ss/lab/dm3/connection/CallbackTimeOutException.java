package ss.lab.dm3.connection;

/**
 * @author Dmitry Goncharov
 */
public class CallbackTimeOutException extends CallbackHandlerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1948400885954988674L;

	/**
	 * @param service 
	 * @param timeout
	 */
	public CallbackTimeOutException(Object service, long timeout) {
		super( "Remote service wait time is out. Service " + service + ". Timeout " + timeout );
	}
	
}
