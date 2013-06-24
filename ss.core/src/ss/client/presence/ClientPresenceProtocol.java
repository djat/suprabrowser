/**
 * 
 */
package ss.client.presence;

import ss.client.presence.handlers.AbstractPresenceEventHandler;
import ss.client.presence.handlers.KeyTypedCommandClientHandler;
import ss.client.presence.handlers.StopTypingCommandClientHandler;
import ss.client.ui.SupraSphereFrame;
import ss.common.presence.AbstractPresenceEvent;
import ss.framework.networking2.Protocol;

/**
 *
 */
public final class ClientPresenceProtocol {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClientPresenceProtocol.class);
	
	private final Protocol protocol;
	
	private final SupraSphereFrame supraSphereFrame;

	
	/**
	 * @param protocol
	 * @param supraSphereFrame
	 */
	public ClientPresenceProtocol(final Protocol protocol, final SupraSphereFrame supraSphereFrame) {
		super();
		this.protocol = protocol;
		this.supraSphereFrame = supraSphereFrame;
		registerHandler( new KeyTypedCommandClientHandler() );
		registerHandler( new StopTypingCommandClientHandler() );
	}

	/**
	 * @param handler
	 */
	private void registerHandler(AbstractPresenceEventHandler handler) {
		handler.initialize(this.supraSphereFrame);
		this.protocol.registerHandler(handler);
	}

	/**
	 * @param event
	 */
	public void fireEvent(AbstractPresenceEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug( "Firing " + event );
		}
		event.fireAndForget( this.protocol );
	}
	
	public void beginClose() {
		this.protocol.beginClose();
	}
	
	
}
