package ss.server.presense.handlers;

import ss.common.presence.AbstractKeyTypedEvent;
import ss.framework.networking2.EventHandlingContext;

public class KeyTypedEventHandler extends AbstractPresenceEventHandler<AbstractKeyTypedEvent> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(KeyTypedEventHandler.class);
	
	/**
	 * @param commandClass
	 */
	public KeyTypedEventHandler() {
		super(AbstractKeyTypedEvent.class);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.EventHandler#handleEvent(ss.framework.networking2.EventHandlingContext)
	 */
	@Override
	protected void handleEvent(EventHandlingContext<AbstractKeyTypedEvent> context) {
		final AbstractKeyTypedEvent event = context.getMessage();
		if (logger.isDebugEnabled()) {
			logger.debug( "Fire back for all " + event );
		}
		super.executeForAll( (AbstractKeyTypedEvent) event.reuse() );
	}





	
}
