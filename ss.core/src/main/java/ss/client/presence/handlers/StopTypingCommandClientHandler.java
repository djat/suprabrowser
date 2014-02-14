package ss.client.presence.handlers;

import ss.client.ui.peoplelist.IPeopleList;
import ss.common.presence.StoppedTypingEvent;

public class StopTypingCommandClientHandler extends AbstractPresenceEventHandler<StoppedTypingEvent> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(StopTypingCommandClientHandler.class);	
	/**
	 * @param commandClass
	 */
	public StopTypingCommandClientHandler() {
		super(StoppedTypingEvent.class);
	}

	/* (non-Javadoc)
	 * @see ss.client.networking.presence.AbstractPresenceEventHandler#handleEvent(ss.common.presence.AbstractPresenceEvent)
	 */
	@Override
	protected void swtHandleEvent(StoppedTypingEvent event) {
		final String typingUser = event.getUserContactName();
		IPeopleList peopleList = super.getPeopleTable( event.getSphereId() );
		if (peopleList != null) {
			boolean setted = peopleList.setAsNotTyping(typingUser);
			if (setted && logger.isDebugEnabled()) {
				logger.debug("seted AsNotTyping " + typingUser);
			}
		}
		logger.info("Cannot set as not typing " + typingUser);
	}
	
	
}
