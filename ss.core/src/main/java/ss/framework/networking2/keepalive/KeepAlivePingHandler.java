/**
 * 
 */
package ss.framework.networking2.keepalive;

import ss.framework.networking2.EventHandler;
import ss.framework.networking2.EventHandlingContext;

/**
 *
 */
public class KeepAlivePingHandler extends EventHandler<KeepAlivePingEvent> {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(KeepAlivePingHandler.class);

	/**
	 * @param notificationClass
	 */
	public KeepAlivePingHandler() {
		super(KeepAlivePingEvent.class);
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.EventHandler#handleEvent(ss.common.networking2.EventHandlingContext)
	 */
	@Override
	protected void handleEvent(EventHandlingContext<KeepAlivePingEvent> context) {
		if ( logger.isDebugEnabled() ) {
			logger.debug( "Received " + context.getMessage() );
		}
	}
	
}