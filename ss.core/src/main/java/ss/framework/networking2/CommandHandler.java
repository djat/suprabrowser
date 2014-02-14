/**
 * 
 */
package ss.framework.networking2;

import ss.common.ArgumentNullPointerException;


/**
 *
 */
public abstract class CommandHandler<C extends Command> extends ActiveMessageHandler<C> {

	/**
	 * @param messageClass
	 */
	public CommandHandler(Class<C> messageClass) {
		super(messageClass);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.IMessageHandler#handle(ss.common.networking2.MessageHandlingContext)
	 */
	public final void handle(MessageHandlingContext<C> context) throws CommandHandleException {
		if ( context == null ) {
			throw new ArgumentNullPointerException( "context" );
		}
		SuccessReply reply = handle( context.getMessage() );
		((CommandHandlingContext<C>)context).reply( reply );
	}
	
	protected abstract SuccessReply handle(C command) throws CommandHandleException;
	
}
