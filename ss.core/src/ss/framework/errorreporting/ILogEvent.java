package ss.framework.errorreporting;

public interface ILogEvent {

	/**
	 * @return the session id (id of log session)
	 */
	long getSessionId();

	/**
	 * @return the message (can be null)
	 */
	String getMessage();

	/**
	 * @return the stack trace (can be null)
	 */
	String getStackTrace();

	/**
	 * @return the code location information (can be null) 
	 */
	String getLocationInformation();

	/**
	 * @return the event level
	 */
	String getLevel();
	
	/**
	 * @return the event context
	 */
	String getContext();

}
