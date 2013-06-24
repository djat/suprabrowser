/**
 * 
 */
package ss.framework.networking2;

import java.io.Serializable;


/**
 *
 */
public abstract class Command extends ActiveMessage {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7315059062545813670L;
	/**
	 * 
	 */
	protected static final int DEFAULT_COMMAND_TIMEOUT = 60000; // 1 minute
	
	private static final int DEFAULT_ASYNC_COMMAND_TIMEOUT = DEFAULT_COMMAND_TIMEOUT * 3; // 3 mins

	/**
	 * Put command to the protocol sending queue.
	 * Reply will be sent to the resultHandler. 
	 * @param protocol target protocol.
	 * @param replyHandler handler that handle result.
	 * @param replyHandlerExecutor resultHandlerExecutor executor that give the Thread that execute handler
	 * @param timeout timeout for command reply waiting.
	 */
	public final void beginExecute(Protocol protocol, ReplyHandler replyHandler, IHandlerExecutor replyHandlerExecutor, int timeout ) {
		protocol.beginExecute( new CommandSendingContext( this, replyHandler, replyHandlerExecutor, timeout ) );			
	}	
	

	/**
	 * @param protocol
	 * @param command_execution_timeout
	 */
	public void beginExecute(Protocol protocol, int timeout ) {
		beginExecute(protocol, protocol.getDefaultResultHandler(), protocol.getDefaultReplyExecutor(), timeout );
	}
	
	/**
	 * Put command to the protocol sending queue.
	 * Reply will be sent to the resultHandler. 
	 * @param protocol target protocol.
	 * @param replyHandler handler that handle result.
	 * @param replyHandlerExecutor resultHandlerExecutor executor that give the Thread that execute handler
	 */
	public final void beginExecute(Protocol protocol, ReplyHandler replyHandler, IHandlerExecutor replyHandlerExecutor ) {
		beginExecute( protocol, replyHandler, replyHandlerExecutor, DEFAULT_ASYNC_COMMAND_TIMEOUT );		
	}	
	
	public final void beginExecute(Protocol protocol, ReplyHandler replyHandler, int timeout ) {
		beginExecute( protocol, replyHandler, protocol.getDefaultReplyExecutor(), timeout );
	}	
	
	/**
	 * Put command to the protocol sending queue.
	 * Reply will be sent to the resultHandler. 
	 * @param protocol target protocol.
	 * @param replyHandler handler that handle result.
	 */
	public final void beginExecute(Protocol protocol, ReplyHandler replyHandler ) {
		beginExecute( protocol, replyHandler, protocol.getDefaultReplyExecutor() );
	}	
	
	/**
	 * Put command to the protocol sending queue.
	 * Result will be sended to the protocol defaultResultHandler.
	 * Same as beginExecute(protocol, protocol.getDefaultResultHandler() ).
	 * @param protocol target protocol
	 */
	public final void beginExecute(Protocol protocol) {
		beginExecute( protocol, protocol.getDefaultResultHandler() );
	}
	
	
	/**
	 * Put command to the protocol sending queue.
	 * Block caller until respose received.
	 * TODO: Throws exception if fail response received.
	 *  
	 * @param <R> success result command   
	 * @param protocol target protocol
	 * @param expectedResponseClass class (or super class) of result command.
	 * @param timeout calling thread block time out
	 * Should be subclass of success result command.
	 * @return command result
	 */
	public final <T extends Serializable> T execute(Protocol protocol, Class<T> expectedResponseClass, int timeout ) throws CommandExecuteException {
		ObjectReplyWaiter<T> replyWaiter = new ObjectReplyWaiter<T>( this, timeout, expectedResponseClass );
		beginExecute(protocol, replyWaiter.getReplyHandler(), protocol.getDefaultReplyExecutor(), timeout);
		replyWaiter.waitReply();
		return replyWaiter.getSuccessReplyObject();
	}
	
	/**
	 * Put command to the protocol sending queue.
	 * Block caller until respose received.
	 * TODO: Throws exception if fail response received.
	 *  
	 * @param protocol target protocol
	 * @param expectedResponseClass class (or super class) of result command.
	 * @param timeout calling thread block time out.
	 * @return command result
	 */
	public final <T extends Serializable> T execute(Protocol protocol, Class<T> expectedResponseClass ) throws CommandExecuteException {
		return this.execute(protocol, expectedResponseClass, DEFAULT_COMMAND_TIMEOUT );
	}

	/**
	 * Put command to the protocol sending queue.
	 * Block caller until respose received.
	 * TODO: Throws exception if fail response received.
	 *  
	 * @param protocol target protocol
	 * @param timeout calling thread block time out
	 */
	public final void execute(Protocol protocol, int timeout ) throws CommandExecuteException {
		VoidReplyWaiter replyWaiter = new VoidReplyWaiter( this, timeout );
		beginExecute(protocol, replyWaiter.getReplyHandler(), protocol.getDefaultReplyExecutor(), timeout);
		replyWaiter.waitReply();
	}
	
	/**
	 * Put command to the protocol sending queue.
	 * Block caller until respose received.
	 * TODO: Throws exception if fail response received.
	 *  
	 * @param protocol target protocol
	 */
	public final void execute(Protocol protocol ) throws CommandExecuteException {
		this.execute(protocol, DEFAULT_COMMAND_TIMEOUT );
	}

}