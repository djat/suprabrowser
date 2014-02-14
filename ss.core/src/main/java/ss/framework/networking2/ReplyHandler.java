/**
 * 
 */
package ss.framework.networking2;

import ss.common.ArgumentNullPointerException;

/**
 *  
 */
public class ReplyHandler implements IMessageHandler<Reply> {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReplyHandler.class);
	
	private final Class expectedSuccessReplyClass;
	
	/**
	 * 
	 */
	public ReplyHandler() {
		this( SuccessReply.class );
	}

	/**
	 */
	public ReplyHandler( Class expectedSuccessReplyClass ) {
		this.expectedSuccessReplyClass = expectedSuccessReplyClass;
		if ( !SuccessReply.class.isAssignableFrom( expectedSuccessReplyClass ) ) {
			throw new IllegalArgumentException( "expectedSuccessReplyClass should be subclass of SuccessReply" );					
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.IMessageHandler#handle(ss.common.networking2.IMessageHandlingContext)
	 */
	public final void handle(MessageHandlingContext<Reply> context) throws CommandHandleException {
		if ( context == null ) {
			throw new ArgumentNullPointerException( "context" );
		}
		ReplyHandlingContext<Reply> castedContext = (ReplyHandlingContext<Reply>) context;
		final Command command = castedContext.getIntiatorCommand();
		final Reply reply = castedContext.getMessage();
		if ( this.expectedSuccessReplyClass.isInstance( reply  ) ) {
			commandSuccessfullyExecuted( command, (SuccessReply) reply );			
		}
		else if ( reply instanceof FailedReply ) {
			commandFailed( command, (FailedReply) reply );
		}
		else {
			commandFailed( command, new FailedReply( "Unexcpected reply " + reply  + " excpected " + this.expectedSuccessReplyClass + "." ) );
		}
	}
	
	/**
	 * Handle success command. 
	 * @param command 
	 * @param successReply success command
	 */
	protected void commandSuccessfullyExecuted(Command command, SuccessReply successReply) {
		// By default do nothing
	}
		
	/**
	 * 
	 * TODO:#think about error handling 
	 * 
	 * Handle failed command.
	 * 
	 * @param failedReply failed command.
	 * @param command 
	 */
	protected final void commandFailed(Command command, FailedReply failedReply) throws CommandHandleException {
		CommandExecuteException exception;
		if (failedReply instanceof TimeoutReply ) {
			exception = ((TimeoutReply) failedReply).createTimeoutException(); 
		}
		else {
			exception = new CommandExecuteFailedException( command, failedReply );	
		}
		exeptionOccured( exception );		
	}
	
	/**
	 * @param exception
	 */
	protected void exeptionOccured(CommandExecuteException exception) throws CommandHandleException {
		throw new CommandHandleException( "Reply failed.", exception );
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " for " + this.expectedSuccessReplyClass;
	}

	
	
}
