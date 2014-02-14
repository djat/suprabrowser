/**
 * 
 */
package ss.framework.networking2;

/**
 * Method can be called from different threads.
 * 
 */
public interface IMessageHandlingResultListener {

	/**
	 * 
	 * @param runner
	 */
	void finished( MessageHandlerRunnableAdaptor runner );

	/**
	 * 
	 * @param runner
	 * @param ex
	 */
	void error( MessageHandlerRunnableAdaptor runner, CommandHandleException ex);

	/**
	 * 
	 * @param runner
	 * @param ex
	 */
	void unexpectedError( MessageHandlerRunnableAdaptor runner, RuntimeException ex);

}
