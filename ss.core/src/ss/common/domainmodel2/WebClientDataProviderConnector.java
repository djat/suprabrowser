/**
 * 
 */
package ss.common.domainmodel2;

import java.util.Hashtable;

import ss.client.networking.NetworkConnectionFactory;
import ss.client.networking.NetworkConnectionProvider;

/**
 * @author roman
 *
 */
public class WebClientDataProviderConnector extends
		AbstractClientDataProviderConnector {

	private final Hashtable<String, String> loginSession;
	
	public WebClientDataProviderConnector(final Hashtable<String, String> loginSession) {
		this.loginSession = loginSession;
	}
	
	@Override
	protected NetworkConnectionProvider createProtocolConnector() {
		return NetworkConnectionFactory.INSTANCE
		.createProvider(DOMAIN_SPACE_PROTOCOL_NAME, this.loginSession);
	}

}
