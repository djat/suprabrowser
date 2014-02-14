/**
 * 
 */
package ss.server.debug;

import ss.common.debug.DebugUtils;
import ss.common.debug.ThreadsDumpCommand;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.RespondentCommandHandler;

/**
 *
 */
public class ThreadsDumpCommandHandler extends RespondentCommandHandler<ThreadsDumpCommand, String>{

	/**
	 * @param acceptableCommandClass
	 */
	public ThreadsDumpCommandHandler() {
		super(ThreadsDumpCommand.class);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected String evaluate(ThreadsDumpCommand command) throws CommandHandleException {
		return DebugUtils.dumpAllThreads( "Server dump");
	}

}
