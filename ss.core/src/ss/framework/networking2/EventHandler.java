/**
 * 
 */
package ss.framework.networking2;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
public abstract class EventHandler<E extends Event> extends ActiveMessageHandler<E> {
			
	/**
	 * @param notificationClass
	 */
	public EventHandler(Class<E> notificationClass) {
		super(notificationClass);
	}

	/**
	 * Perform command handling
	 * @param command not null packet
	 */
	public final void handle( MessageHandlingContext<E> context ) throws CommandHandleException {
		if ( context == null ) {
			throw new ArgumentNullPointerException( "context" );
		}
		final EventHandlingContext<E> castedContext = (EventHandlingContext<E>) context;
		handleEvent( castedContext );
	}

	/**
	 * @param message
	 */
	protected abstract void handleEvent(EventHandlingContext<E> context);
	
}
