/**
 * 
 */
package ss.framework.networking2;

import java.io.Serializable;

/**
 *
 */
public final class ObjectReplyWaiter<R extends Serializable> extends AbstractReplyWaiter {

	private volatile R successReplyObject;
	
	private class ReplyHandler extends ReplyObjectHandler<R> {

		/**
		 * @param expectedReplyObject
		 */
		private ReplyHandler(Class<R> expectedReplyObject) {
			super(expectedReplyObject);
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.AbstractReceiveObjectPch#handleReply(java.io.Serializable)
		 */
		@Override
		protected void objectReturned(R reply) {
			setSuccessReplyObject(reply);
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
	 * @param timeout
	 */
	public ObjectReplyWaiter(Command command, int timeout, Class<R> expectedReplyClass ) {
		super( command, timeout );
		this.replyHandler = new ReplyHandler( expectedReplyClass ); 
	}
	
	
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.AbstractReplyWaiter#getReplyHandler()
	 */
	@Override
	public ReplyHandler getReplyHandler() {
		return this.replyHandler; 
	}

	/**
	 * @param replyObject the replyObject to set
	 */
	private void setSuccessReplyObject(R replyObject) {
		this.successReplyObject = replyObject;
		afterReplyReceived();
	}
	
	/**
	 * @return the replyObject
	 */
	public final R getSuccessReplyObject() {
		return this.successReplyObject;
	}
	

}
