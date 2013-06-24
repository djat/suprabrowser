package ss.common.domainmodel2;

import java.io.IOException;

import ss.client.networking.NetworkConnectionProvider;
import ss.client.networking2.ClientProtocolManager;
import ss.common.ArgumentNullPointerException;
import ss.common.threads.ThreadBlocker;
import ss.common.threads.ThreadBlocker.TimeOutException;
import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.IDataProvider;
import ss.framework.domainmodel2.IDataProviderConnector;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.ProtocolLifetimeAdapter;
import ss.framework.networking2.ProtocolLifetimeEvent;

public abstract class AbstractClientDataProviderConnector implements IDataProviderConnector {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractClientDataProviderConnector.class);
	
	/**
	 * 
	 */
	private static final int PROTOCOL_START_UP_TIMEOUT = 30000;

	public final static String DOMAIN_SPACE_PROTOCOL_NAME = "SurpaClientDataProviderConnector";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.domainmodel2.IDataProviderConnector#reconnect(ss.framework.domainmodel2.AbstractDomainSpace)
	 */
	public void reconnect(final AbstractDomainSpace space) {
		if ( space == null ) {
			throw new ArgumentNullPointerException( "space" );
		}
		logger.debug("creating connector");
		final NetworkConnectionProvider connector = createProtocolConnector();
		final Protocol protocol;
		try {
			protocol = connector.openProtocol("DM2C["
					+ connector.getUserLogin() + "]");
		} catch (IOException ex) {
			// TODO:#make separate type
			throw new RuntimeException(ex);
		}
		final IDataProvider dataProvider = new NetworkDataProvider(protocol,
				space.getId());
		final ThreadBlocker threadBlocker = new ThreadBlocker(
				PROTOCOL_START_UP_TIMEOUT);
		protocol.addProtocolListener(new ProtocolLifetimeAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see ss.common.networking2.ProtocolLifetimeAdapter#beginClose(ss.common.networking2.ProtocolLifetimeEvent)
			 */
			@Override
			public void beginClose(ProtocolLifetimeEvent e) {
				super.beginClose(e);
				logger.info("begin client domain protocol closing.");
				space.resetDataProvider(dataProvider);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see ss.common.networking2.ProtocolLifetimeAdapter#started(ss.common.networking2.ProtocolLifetimeEvent)
			 */
			@Override
			public void started(ProtocolLifetimeEvent e) {
				super.started(e);
				threadBlocker.release();
			}
		});
		logger.info("starting new domain protocol " + protocol);
		protocol.start(ClientProtocolManager.INSTANCE);
		space.setDataProvider(dataProvider);
		try {
			threadBlocker.blockUntilRelease();
			logger.info("client domain reconnected via " + protocol);
		} catch (TimeOutException ex) {
			logger.error( "Fail to reconnect client domain", ex);
		}
	}

	/**
	 * @return
	 */
	protected abstract NetworkConnectionProvider createProtocolConnector();

}
