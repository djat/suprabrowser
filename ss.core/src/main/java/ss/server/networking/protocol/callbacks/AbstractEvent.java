package ss.server.networking.protocol.callbacks;

import ss.framework.networking2.Event;
import ss.server.networking.DialogsMainPeer;

public abstract class AbstractEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3351660999961377385L;

	/**
	 * @param handler
	 */
	public void fireAndForget(DialogsMainPeer peer) {
		fireAndForget( peer.getProtocol() );
	}
	
}
