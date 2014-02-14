/**
 * 
 */
package ss.framework.networking2.executors;

import ss.common.threads.ObjectRefusedException;
import ss.common.threads.SingleTheradExecutor;
import ss.framework.networking2.IHandlerExecutor;
import ss.framework.networking2.MessageHandlerRunnableAdaptor;

/**
 *
 */
public final class SingleThreadHandlerExecutor implements IHandlerExecutor {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SingleThreadHandlerExecutor.class);
	
	private SingleTheradExecutor<MessageHandlerRunnableAdaptor> impl; 
	/**
	 * @param subName
	 */
	public SingleThreadHandlerExecutor(String subName) {
		this.impl = new SingleTheradExecutor<MessageHandlerRunnableAdaptor>( subName );
	}
	
	/**
	 * 
	 * @see ss.common.threads.SingleTheradExecutor#shootdown()
	 */
	public void shootdown() {
		this.impl.shootdown();
	}

	/**
	 * @param baseName
	 * @see ss.common.threads.SingleTheradExecutor#start(java.lang.String)
	 */
	public void start(String baseName) {
		this.impl.start(baseName);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.IHandlerExecutor#beginExecute(ss.framework.networking2.MessageHandlerRunnableAdaptor)
	 */
	public synchronized void beginExecute(MessageHandlerRunnableAdaptor runnable) {
		try {
			this.impl.beginExecute(runnable);
		} catch (ObjectRefusedException ex) {
			logger.error( "Message handler runable adaptor refused.", ex );
		}
		
	}

}