/**
 * 
 */
package ss.framework.networking2;

import ss.common.ArgumentNullPointerException;
import ss.common.IdentityUtils;

/**
 *
 */
abstract class MessageSendingContext {

	private final Message message;

	/**
	 * @param message
	 * @param resultHandler
	 * @param resultHandlerExecutor
	 */
	public MessageSendingContext(Message message) {
		super();
		if ( message == null ) {
			throw new ArgumentNullPointerException( "message" );
		}
		this.message = message;
	}

	final void prepareToSend() {
		this.message.frozeMessage( IdentityUtils.generateUuid().toString() );
	}

	/**
	 * @return the command
	 */
	public final Message getMessageToSend() {
		return this.message;
	}

	/**
	 * @return command id
	 */
	public final String getMessageSendId() {
		return this.message.getSendId();
	}	
	
}
