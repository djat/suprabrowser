package ss.client.errorreporting;

import java.io.IOException;

import ss.client.debug.DebugProtocolFactory;
import ss.client.networking.NetworkConnectionProvider;
import ss.client.networking.NetworkConnectionFactory;
import ss.framework.errorreporting.LogConstants;
import ss.framework.errorreporting.network.AbstractNetworkLogEventTransport;
import ss.framework.errorreporting.network.CreateProtocolResult;
import ss.framework.networking2.Protocol;
import ss.framework.threads.CantInitializeException;

public class ClientLogEventTransport extends AbstractNetworkLogEventTransport {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClientLogEventTransport.class);
	
	/**
	 * 
	 */
	private static final int SLEEP_PERIOD = 5000; 

	private static final int INITIALIZING_TIMEOUT = 60000 * 2; // two mins

	
	/* (non-Javadoc)
	 * @see ss.framework.errorreporting.network.AbstractNetworkLogEventTransport#createProtocol()
	 */
	@Override
	protected CreateProtocolResult createProtocol() throws CantInitializeException {
		final long deadLine = System.currentTimeMillis() + INITIALIZING_TIMEOUT;
		while (System.currentTimeMillis() < deadLine) {
			try {
				Thread.sleep(SLEEP_PERIOD);
			} catch (InterruptedException ex) {
				logger.error("Can't wait to initialize", ex);
				throw new CantInitializeException(ex);
			}			
			if ( NetworkConnectionFactory.INSTANCE.isInitializedByDefault() ||
				 DebugProtocolFactory.INSTANCE.hasDesiredStartUpSessionFactory() ) {
				break;
			}
		}
		if (!NetworkConnectionFactory.INSTANCE.isInitializedByDefault() &&
			!DebugProtocolFactory.INSTANCE.hasDesiredStartUpSessionFactory() ) {
			throw new CantInitializeException(
					"Can't initialize because SupraProtocolConnectorFactory is not ready. Initialization timeout: " + INITIALIZING_TIMEOUT );
		}
		final NetworkConnectionProvider connector = NetworkConnectionFactory.INSTANCE
				.createProvider( LogConstants.LOG_PROTOCOL_NAME, DebugProtocolFactory.INSTANCE.getDesiredStartUpSessionFactory() );
		try {
			final Protocol protocol = connector.openProtocol( LogConstants.LOG_PROTOCOL_NAME );
			return new CreateProtocolResult( protocol,
					connector.getSessionKey(),
					connector.getUserLogin() );
		} catch (IOException ex) {
			throw new CantInitializeException(ex);
		}
	}
}
