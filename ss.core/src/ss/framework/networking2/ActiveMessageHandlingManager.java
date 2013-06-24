/**
 * 
 */
package ss.framework.networking2;

import java.util.Hashtable;

import ss.common.ArgumentNullPointerException;
import ss.common.operations.OperationBreakException;


/**
 *
 */
final class ActiveMessageHandlingManager {

	@SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ActiveMessageHandlingManager.class);
	
	private final Protocol protocolOwner;
	
	private final Hashtable<Class, ActiveMessageDispatcher> messageClassToDispatcher = new Hashtable<Class, ActiveMessageDispatcher>();
			
	/**
	 * TODO: may be remove protocolOwner 
	 * @param protocolOwner
	 */
	public ActiveMessageHandlingManager(final Protocol protocolOwner) {
		super();
		this.protocolOwner = protocolOwner;
	}

	/**
	 * @param protocol
	 * @param command
	 */
	public final void dispatchMessage(ActiveMessage message) throws OperationBreakException {
		if ( message == null ) {
			throw new ArgumentNullPointerException( "message" );
		}
		final ActiveMessageDispatcher dispatcher = findDispatcher(message.getClass());
		if ( dispatcher != null ) {
			dispatcher.dispachMessage( message );
		}
		else {
			this.protocolOwner.handlerNotFound( message, "Cannot find message handler by message class." );			
		}
	}

	/**
	 * Registry message dispatcher
	 */
	private final void addDispatcher(ActiveMessageDispatcher dispatcher) {
		if (dispatcher == null) {
			throw new ArgumentNullPointerException("dispatcher");
		}		
		final Class acceptableMessageClass = dispatcher.getAcceptableMessageClass();
		if (this.messageClassToDispatcher.contains(acceptableMessageClass)) {
			throw new IllegalArgumentException("Message with class "
					+ acceptableMessageClass + " already have executable message handler "
					+ this.messageClassToDispatcher.get(acceptableMessageClass) + ".");
		}
		this.messageClassToDispatcher.put(acceptableMessageClass, dispatcher);		
	}
		
	/**
	 * Returns command handler execution bundle that can handle the command
	 * 
	 * @param command
	 * @return command handler execution bundle that can handle the commandor null if no handler execution bundle 
	 *  		was found.
	 */
	final ActiveMessageDispatcher findDispatcher(Class acceptableMessageClass) {
		while (acceptableMessageClass != Object.class) {
			ActiveMessageDispatcher handler = this.messageClassToDispatcher.get(acceptableMessageClass);
			if (handler != null) {
				return handler;
			}
			acceptableMessageClass = acceptableMessageClass.getSuperclass();
		}
		return null;
	}
	
	/**
	 * @param handler
	 * @param handlerExecutor
	 */
	public final void addHandler(ActiveMessageHandler handler, IHandlerExecutor handlerExecutor) {
		addDispatcher( createMessageDispatcher( handler, handlerExecutor ) );
	}
	
	private final ActiveMessageDispatcher createMessageDispatcher( ActiveMessageHandler handler, IHandlerExecutor handlerExecutor ) {
		if ( handler == null ) {
			throw new ArgumentNullPointerException( "handler" );
		}
		if ( handler instanceof CommandHandler ) {
			return new CommandDispatcher( this, (CommandHandler)handler, handlerExecutor );			
		}
		else if ( handler instanceof EventHandler ) {
			return new EventDispatcher( this, (EventHandler)handler, handlerExecutor );			
		}
		else {
			 throw new IllegalArgumentException( "Unsupported handler " + handler );
		}
	}

	/**
	 * @return the protocolOwner
	 */
	public final Protocol getProtocolOwner() {
		return this.protocolOwner;
	}

	
}
