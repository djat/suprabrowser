package ss.smtp;

import java.util.Hashtable;

import ss.client.networking.CantCreateConnectorException;
import ss.client.networking.SupraClient;
import ss.client.networking.NetworkConnectionProvider;
import ss.common.domainmodel2.AbstractClientDataProviderConnector;

final class SmtpDataProviderConnector extends AbstractClientDataProviderConnector {

	/* (non-Javadoc)
	 * @see ss.common.domainmodel2.AbstractClientDataProviderConnector#createProtocolConnector()
	 */
	@Override
	protected NetworkConnectionProvider createProtocolConnector() throws CantCreateConnectorException {
		SupraClient sc = new SupraClient();
		Hashtable loginSession = sc.loadMachineServerAuthProperties();
		Object protocolConnectorObject = sc.startZeroKnowledgeAuth( loginSession, AbstractClientDataProviderConnector.DOMAIN_SPACE_PROTOCOL_NAME );
		if(protocolConnectorObject==null || !(protocolConnectorObject instanceof NetworkConnectionProvider)) {
			throw new CantCreateConnectorException();
		}
		NetworkConnectionProvider supraProtocolConnector = (NetworkConnectionProvider) protocolConnectorObject;
		return supraProtocolConnector;
	}

}
