/**
 * 
 */
package ss.framework.networking2.executors;

import ss.common.ThreadUtils;
import ss.framework.networking2.*;

/**
 *
 */
public final class ThreadPerHandlerExecutor implements IHandlerExecutor {
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.AbstractHandlerExecutor#execute(ss.common.networking2.CommandHandlerRunner)
	 */
	public void beginExecute(MessageHandlerRunnableAdaptor runner) {
		ThreadUtils.start(runner, runner.getHandler().getClass() );
	}

}
