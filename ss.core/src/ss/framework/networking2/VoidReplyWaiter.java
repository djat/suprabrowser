/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public class VoidReplyWaiter extends AbstractReplyWaiter {

	private class ReplyHandler extends ReplyVoidHandler {

		/* (non-Javadoc)
		 * @see ss.common.networking2.ReplyVoidHandler#commandExecuted()
		 */
		@Override
		protected void commandExecuted() {
			super.commandExecuted();
			afterReplyReceived();
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.AbstractReceiveObjectPch#handleException(ss.common.networking2.CommandExecuteException)
		 */
		@Override
		protected void exeptionOccured(CommandExecuteException exception) {
			setFailedReplyObject(exception);
		}		
	}
	
	private final ReplyHandler replyHandler;
	
	/**
	 * @param command
	 * @param timeout
	 */
	public VoidReplyWaiter(Command command, int timeout) {
		super(command, timeout);
		this.replyHandler = new ReplyHandler();
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.AbstractReplyWaiter#getReplyHandler()
	 */
	@Override
	public ss.framework.networking2.ReplyHandler getReplyHandler() {
		return this.replyHandler;
	}
}
