/**
 * 
 */
package ss.framework.networking2;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
abstract class MessageHandlingContext<M extends Message> {
	
	private final M message;
	
	/**
	 * @param message
	 */
	public MessageHandlingContext(final M message) {
		super();
		if ( message == null ) {
			throw new ArgumentNullPointerException( "message" );
		}
		this.message = message;
	}

	public final M getMessage() {
		return this.message;
	}
	
	public final Class getMessageClass() {
		return this.message.getClass();
	}
	
}
