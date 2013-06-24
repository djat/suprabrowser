/**
 * 
 */
package ss.client.presence;

import java.io.IOException;
import java.util.Hashtable;

import ss.client.networking.NetworkConnectionProvider;
import ss.client.networking.NetworkConnectionFactory;
import ss.client.networking2.ClientProtocolManager;
import ss.client.ui.MessagesPane;
import ss.common.presence.PresenceUtils;
import ss.framework.networking2.Protocol;

/**
 *
 */
public final class ClientPresenceManager {

	/**
	 * Singleton instance
	 */
	public final static ClientPresenceManager INSTANCE = new ClientPresenceManager();

	private final Hashtable<MessagesPane, ClientPresence> messagesPaneToClientPresence = new Hashtable<MessagesPane, ClientPresence>();
	
	private ClientPresenceProtocol clientPresenceProtocol = null;
	
	private ClientPresenceManager() {
	}
	
	/**
	 * @return the clientPresenceProtocol
	 */
	private ClientPresenceProtocol getClientPresenceProtocol() {
		if ( this.clientPresenceProtocol == null ) {
			this.clientPresenceProtocol = createClientPresence(); 
		}
		return this.clientPresenceProtocol;
	}

	/**
	 * @return
	 */
	private ClientPresenceProtocol createClientPresence() {
		final NetworkConnectionProvider connector = NetworkConnectionFactory.INSTANCE.createProvider( PresenceUtils.PRESENCE_PROTOCOL_NAME );
		final Protocol protocol;
		try {
			protocol = connector.openProtocol( "Presence[" + connector.getUserLogin() + "]" );
		} catch (IOException ex) {
			throw new CantCreateClientPresenceProtocolException( ex);
		}
		ClientPresenceProtocol clientPresenceProtocol = new ClientPresenceProtocol( protocol, connector.getSupraSphereFrame() );
		// Only now start protocol. When all handlers was registered.
		protocol.start( ClientProtocolManager.INSTANCE );
		return clientPresenceProtocol;
	}


	/**
	 * @param pane
	 * @return
	 */
	public synchronized ClientPresence getClientPresence(MessagesPane pane) {
		ClientPresence clientPresence = this.messagesPaneToClientPresence.get(pane);
		if ( clientPresence == null ) {
			clientPresence = createClientPresence( pane );
			this.messagesPaneToClientPresence.put(pane, clientPresence);
		}
		return clientPresence;
	}
	
	/**
	 * @param messagesPaneOwner
	 * @return
	 */
	private ClientPresence createClientPresence(MessagesPane messagesPaneOwner) {
		return new ClientPresence( getClientPresenceProtocol(), messagesPaneOwner );
	}

	/**
	 *
	 */
	public static class CantCreateClientPresenceProtocolException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1976457760664126961L;

		/**
		 * @param ex
		 */
		public CantCreateClientPresenceProtocolException(IOException ex) {
			super( "Can't create client presence protocol", ex );
		}

	}
}
