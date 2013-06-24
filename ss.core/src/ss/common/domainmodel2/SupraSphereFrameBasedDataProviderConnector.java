package ss.common.domainmodel2;

import ss.client.networking.NetworkConnectionProvider;
import ss.client.networking.NetworkConnectionFactory;

public final class SupraSphereFrameBasedDataProviderConnector extends AbstractClientDataProviderConnector {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraSphereFrameBasedDataProviderConnector.class);

	@Override
	protected NetworkConnectionProvider createProtocolConnector() {
		return NetworkConnectionFactory.INSTANCE
				.createProvider(DOMAIN_SPACE_PROTOCOL_NAME);
	}


}