/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public class FireAndForgetMessageSendingContext extends MessageSendingContext {

	public FireAndForgetMessageSendingContext(Event event) {
		super(event);
	}
	
	public FireAndForgetMessageSendingContext(Reply message) {
		super(message);
	}

}
