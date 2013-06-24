package ss.server.presense.handlers;

import ss.common.networking2.ProtocolStartUpInformation;
import ss.common.presence.AbstractKeyTypedEvent;
import ss.common.presence.AbstractPresenceEvent;
import ss.framework.domainmodel2.LockedIterable;
import ss.framework.networking2.EventHandler;
import ss.framework.networking2.Protocol;
import ss.server.networking2.ServerProtocolManager;
import ss.server.presense.ServerPresence;

public abstract class AbstractPresenceEventHandler<E extends AbstractPresenceEvent> extends EventHandler<E>{

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractPresenceEventHandler.class);
	
	private ProtocolStartUpInformation setUpInformation;
	/**
	 * @param commandClass
	 */
	public AbstractPresenceEventHandler(Class<E> commandClass) {
		super( commandClass );
	}

	/**
	 * @param setUpInformation
	 */
	public void initialize(ProtocolStartUpInformation setUpInformation) {
		this.setUpInformation = setUpInformation;		
	}
	
	/**
	 * @param event
	 */
	protected void executeForAll(AbstractKeyTypedEvent event) {
		LockedIterable<Protocol> items = ServerProtocolManager.INSTANCE.getIndex().select(
				ServerPresence.PRESENCE_MARK);
		if (logger.isDebugEnabled()) {
			logger.debug( "Fire back event " + event + " to " + items );
		}		
		try {
			event.fireAndForget( items );
		} finally {
			items.release();
		}
	}

	/**
	 * @return
	 */
	public String getUserLogin() {
		return this.setUpInformation.getUserLogin();
	}

	/**
	 * @return
	 */
	public String getSupraSphereId() {
		return this.setUpInformation.getSupraSphereId();
	}

	
};
