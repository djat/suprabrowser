/**
 * 
 */
package ss.framework.networking2;

import ss.common.ArgumentNullPointerException;
import ss.common.threads.ThreadBlocker;
import ss.common.threads.ThreadBlocker.TimeOutException;

/**
 *
 */
abstract class AbstractReplyWaiter {

	/**
	 * 
	 */
	private static final int WAIT_TO_KILL_BY_MANAGER = 2000;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractReplyWaiter.class);
	
	private volatile CommandExecuteException failedReplyObject;
		
	private final ThreadBlocker replyReceived;   
	
	private final Command command;

	/**
	 * @param timeout
	 */
	public AbstractReplyWaiter(Command command, int timeout ) {
		super();
		if ( command == null ) {
			throw new ArgumentNullPointerException( "command" );
		}
		this.command = command;
		// Expand time out. Because ReplyHandlingManager should return "timeout reply" before 
		// ThreadBlocker timeout
		this.replyReceived = new ThreadBlocker(timeout + WAIT_TO_KILL_BY_MANAGER );
	}

	/**
	 * @return
	 */
	public abstract ReplyHandler getReplyHandler();

	/**
	 * @return
	 */
	protected void waitReply() throws CommandExecuteException {
		try {
			this.replyReceived.blockUntilRelease();
			if ( isSuccessfullyReply() ) {
				return;
			}
			else {
				throw getFailedReplyObject();
			}
		}
		catch( TimeOutException ex ) {
			throw new CommandExecuteTimeoutException( this.command, ex.getTimeout(), ex.getStartupTime() );
		}
	}

	/**
	 * @return
	 */
	private boolean isSuccessfullyReply() {
		return this.failedReplyObject == null;
	}

	/**
	 * 
	 */
	protected final void afterReplyReceived() {
		if ( !this.replyReceived.release() ) {
			throw new IllegalStateException( "Reply already received." );
		}
	}

	/**
	 * @param failedException the failedException to set
	 */
	protected final void setFailedReplyObject(CommandExecuteException failedException) {
		this.failedReplyObject = failedException;
		afterReplyReceived();
	}

	/**
	 * @return the failedException
	 */
	private CommandExecuteException getFailedReplyObject() {
		return this.failedReplyObject;
	}
}
