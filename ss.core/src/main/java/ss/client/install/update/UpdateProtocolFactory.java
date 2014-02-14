/**
 * 
 */
package ss.client.install.update;

import java.io.IOException;
import java.util.Hashtable;

import ss.client.networking.CantCreateConnectorException;
import ss.client.networking.NetworkConnectionProvider;
import ss.client.networking.NetworkConnectionFactory;
import ss.common.ArgumentNullPointerException;
import ss.common.InstallUtils;
import ss.framework.install.update.CantCreateUpdateProtocolException;
import ss.framework.install.update.IUpdateProtocolFactory;
import ss.framework.networking2.Protocol;


/**
 *
 */
public class UpdateProtocolFactory implements IUpdateProtocolFactory  {

	private final Hashtable<String,String> sessionForLogin;
	
	/**
	 * @param sessionForLogin
	 */
	public UpdateProtocolFactory(final Hashtable<String, String> sessionForLogin) {
		super();
		if (sessionForLogin == null) {
			throw new ArgumentNullPointerException("sessionForLogin");
		} 
		this.sessionForLogin = sessionForLogin;
	}

	/* (non-Javadoc)
	 * @see ss.framework.install.update.IUpdateProtocolFactory#create()
	 */
	public Protocol create() throws CantCreateUpdateProtocolException {
		final NetworkConnectionProvider connector;
		try {
			connector = NetworkConnectionFactory.INSTANCE.createProvider( InstallUtils.UPDATE_PROTOCOL_NAME, this.sessionForLogin );
		} catch (CantCreateConnectorException ex ) {
			throw new CantCreateUpdateProtocolException( "Can't create protocol connection provider", ex );
		}
		try {
			return connector.openProtocol( "UpdateProtocol" );
		} catch (IOException ex) {
			throw new CantCreateUpdateProtocolException( "Can't create protocol connection", ex );
		}
	}

		
	
}
