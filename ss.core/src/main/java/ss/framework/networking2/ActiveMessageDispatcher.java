/**
 * 
 */
package ss.framework.networking2;

import ss.common.ArgumentNullPointerException;

/**
 * 
 */
abstract class ActiveMessageDispatcher {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ActiveMessageDispatcher.class);
	
	private final ActiveMessageHandlingManager managerOwner;
	
	private final ActiveMessageHandler handler;
	
	private final IHandlerExecutor handlerExecutor;

	/**
	 * @param handler
	 * @param handlerExecutor
	 */
	public ActiveMessageDispatcher(ActiveMessageHandlingManager manager, ActiveMessageHandler handler, IHandlerExecutor handlerExecutor ) {
		super();
		if ( manager == null ) {
			throw new ArgumentNullPointerException( "manager" );
		}
		if ( handler == null ) {
			throw new ArgumentNullPointerException( "handler" );
		}
		if ( handlerExecutor == null ) {
			throw new ArgumentNullPointerException( "handlerExecutor" );
		}		
		this.managerOwner = manager;
		this.handler = handler;
		this.handlerExecutor = handlerExecutor;		
		
	}
	
	public abstract void dispachMessage( ActiveMessage message );
	
	protected final void executeHandler( ActiveMessageHandlingContext context ) {
		if (logger.isDebugEnabled()) {
			logger.debug( "Before execute " + context );
		}
		this.handlerExecutor.beginExecute( createHandlerRunnableAdaptor(context) );
	}
	
	/**
	 * @param context
	 * @return
	 */
	private final MessageHandlerRunnableAdaptor createHandlerRunnableAdaptor(ActiveMessageHandlingContext context) {
		return new MessageHandlerRunnableAdaptor( this.handler, context, getResultListener() );
	}

	/**
	 * @return
	 */
	protected abstract IMessageHandlingResultListener getResultListener();

	/**
	 * @return
	 */
	public final Class getAcceptableMessageClass() {
		return this.handler.getAcceptableMessageClass();
	}

	public final Protocol getProtocolOwner() {
		return getManagerOwner().getProtocolOwner();
	}

	/**
	 * @return the manager
	 */
	public ActiveMessageHandlingManager getManagerOwner() {
		return this.managerOwner;
	}
	
	
}
