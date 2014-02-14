/**
 * 
 */
package ss.framework.networking2;

/**
 * Execute message handler 
 */
public interface IHandlerExecutor {
	
	/**
	 * @param runnable
	 */
	void beginExecute(MessageHandlerRunnableAdaptor runnable);
	
}