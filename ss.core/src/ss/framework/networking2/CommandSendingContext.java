/**
 * 
 */
package ss.framework.networking2;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
final class CommandSendingContext extends MessageSendingContext implements IReplyDispatcher {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CommandSendingContext.class);
	
	private final static IMessageHandlingResultListener RESULT_LISTENER = new LoggerMessageHandingResultListener( logger );
	
	private final ReplyHandler replyHandler; 
	
	private final IHandlerExecutor replyHandlerExecutor;
	
	private final int timeout;
	

	/**
	 * @param command
	 * @param replyHandler
	 * @param replyHandlerExecutor
	 */
	public CommandSendingContext(Command command, ReplyHandler replyHandler, IHandlerExecutor replyHandlerExecutor, int timeout) {
		super( command );
		if ( replyHandler == null ) {
			throw new ArgumentNullPointerException( "replyHandler" ); 
		}
		if ( replyHandlerExecutor == null ) {
			throw new ArgumentNullPointerException( "replyHandlerExecutor" );
		}
		this.replyHandler = replyHandler;
		this.replyHandlerExecutor = replyHandlerExecutor;
		this.timeout = timeout;
	}

	/**
	 * @param ex
	 */
	public final void cannotSendInitiationCommand(InterruptedException ex) {
		dispachReply( new FailedReply( "Cannot send.", ex ) );
	}

	/**
	 * 
	 */
	public final void cannotSendInitiationCommand() {
		dispachReply( new FailedReply( "Cannot send. Cause: protocol not ready." ) );
	}

	/**
	 * @param reply
	 */
	public final void dispachReply( Reply reply) {
		this.replyHandlerExecutor.beginExecute( createRunnableAdaptor(reply) );
	}

	/**
	 * @param reply
	 * @return
	 */
	private MessageHandlerRunnableAdaptor createRunnableAdaptor(Reply reply) {
		return new MessageHandlerRunnableAdaptor( this.replyHandler, 
				new ReplyHandlingContext<Reply>( reply, (Command) getMessageToSend() ), 
				getResultListener() );
	}

	/**
	 * @return
	 */
	private IMessageHandlingResultListener getResultListener() {
		return RESULT_LISTENER;
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.IReplyReceiver#getInitiationCommandId()
	 */
	public String getInitiationSendId() {
		return getMessageSendId();
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.IReplyDispatcher#replyTimeOut()
	 */
	public void replyTimeOut() {
		dispachReply( new TimeoutReply( (Command) this.getMessageToSend(), this.timeout ) );		
	}	
	
}
