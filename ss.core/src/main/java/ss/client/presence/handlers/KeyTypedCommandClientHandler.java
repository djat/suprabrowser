package ss.client.presence.handlers;

import ss.client.ui.peoplelist.IPeopleList;
import ss.common.presence.KeyTypedEvent;

public class KeyTypedCommandClientHandler extends
		AbstractPresenceEventHandler<KeyTypedEvent> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(KeyTypedCommandClientHandler.class);


	/**
	 * @param commandClass
	 */
	public KeyTypedCommandClientHandler() {
		super(KeyTypedEvent.class);
	}

	/* (non-Javadoc)
	 * @see ss.client.networking.presence.AbstractPresenceEventHandler#handleEvent(ss.common.presence.AbstractPresenceEvent)
	 */
	@Override
	protected void swtHandleEvent(KeyTypedEvent event) {
		final String typingUser = event.getUserContactName();
		final String replyId = event.getReplyId();
		IPeopleList peopleList = super.getPeopleTable( event.getSphereId() );
		if (peopleList != null) {
			boolean setted = peopleList.setAsTyping(typingUser, replyId);
			if (setted && logger.isDebugEnabled()) {
				logger.debug("seted AsTyping " + typingUser);
			}
			return;
		}
		logger.info("Cannot set as typing " + typingUser);
	}
	
}
