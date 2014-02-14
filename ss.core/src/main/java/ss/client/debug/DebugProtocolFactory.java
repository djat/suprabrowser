/**
 * 
 */
package ss.client.debug;

import java.io.IOException;

import ss.client.networking.IStartUpSessionFactory;
import ss.client.networking.StartUpArgsHook;
import ss.client.networking.NetworkConnectionProvider;
import ss.client.networking.NetworkConnectionFactory;
import ss.client.networking2.ClientProtocolManager;
import ss.common.debug.DebugUtils;
import ss.framework.networking2.Protocol;

/**
 *
 */
public class DebugProtocolFactory {

	/**
	 * Singleton instance
	 */
	public final static DebugProtocolFactory INSTANCE = new DebugProtocolFactory();

	private volatile IStartUpSessionFactory explicitStartUpSessionFactory;
	
	private DebugProtocolFactory() {
	}
	
	public Protocol create() {
		IStartUpSessionFactory startUpSessionFactory = getDesiredStartUpSessionFactory();
		NetworkConnectionProvider connector = NetworkConnectionFactory.INSTANCE.createProvider(DebugUtils.DEBUG_PROTOCOL_NAME, startUpSessionFactory );
		try {
			Protocol protocol = connector.openProtocol( "Debug" );
			protocol.start( ClientProtocolManager.INSTANCE );
			return protocol;
		} catch (IOException ex) {
			connector.forceClose();
			throw new RuntimeException("Can't create protocol", ex );
		}		
	}

	/**
	 * @return
	 */
	public synchronized IStartUpSessionFactory getDesiredStartUpSessionFactory() {
		IStartUpSessionFactory startUpSessionFactory = this.explicitStartUpSessionFactory;
		if ( startUpSessionFactory == null ) {
			startUpSessionFactory = StartUpArgsHook.INSTANCE.getLastGoodStartUpSessionFactory();
		}
		return startUpSessionFactory;
	}
	
	public boolean hasDesiredStartUpSessionFactory() {
		return getDesiredStartUpSessionFactory() != null;
	}

	/**
	 * @param startUpSessionFactory
	 */
	public synchronized void setExplicitStartUpSessionFactory(IStartUpSessionFactory startUpSessionFactory) {
		this.explicitStartUpSessionFactory = startUpSessionFactory;
	}

	/**
	 * @return the startUpSessionFactory
	 */
	public synchronized IStartUpSessionFactory getExplicitStartUpSessionFactory() {
		return this.explicitStartUpSessionFactory;
	}
	
	
}
