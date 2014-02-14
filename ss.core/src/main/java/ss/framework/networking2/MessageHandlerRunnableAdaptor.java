/**
 * 
 */
package ss.framework.networking2;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
public final class MessageHandlerRunnableAdaptor implements Runnable {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessageHandlerRunnableAdaptor.class);
	
	private final IMessageHandler handler;
	
	private final MessageHandlingContext context;
	
	private final IMessageHandlingResultListener resultListener;

	/**
	 * @param handler
	 * @param context
	 * @param resultListener
	 */
	public MessageHandlerRunnableAdaptor(final IMessageHandler handler, final MessageHandlingContext context, final IMessageHandlingResultListener resultListener) {
		super();
		if ( handler == null ) {
			throw new ArgumentNullPointerException( "handler" );
		}
		if ( context == null ) {
			throw new ArgumentNullPointerException( "command" );
		}
		if ( resultListener == null ) {
			throw new ArgumentNullPointerException( "resultListener" );
		}
		this.handler = handler;
		this.context = context;
		this.resultListener = resultListener;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug( "Before call handler " + this.handler );
			}
			this.handler.handle(this.context);
			if (logger.isDebugEnabled()) {
				logger.debug( "Handler called " + this.handler );
			}
			this.resultListener.finished( this );
		}
		catch (CriticalCommandHandleException ex) {
			this.resultListener.error( this, ex );
		}
		catch (CommandHandleException ex) {
			this.resultListener.error( this, ex );
		}
		catch( RuntimeException ex ) {
			this.resultListener.unexpectedError( this, ex );
		}
		catch( Throwable ex ) {
			logger.warn( "Command handler execution failed", ex );
		}		
	}

	/**
	 * @return the message handling context
	 */
	public final MessageHandlingContext getContext() {
		return this.context;
	}

	/**
	 * @return the handler
	 */
	public final IMessageHandler getHandler() {
		return this.handler;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Runable adaptor for " + this.handler;
	}
	
	
	
}
