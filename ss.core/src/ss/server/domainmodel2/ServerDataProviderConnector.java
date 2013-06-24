package ss.server.domainmodel2;

import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.IDataProviderConnector;

public class ServerDataProviderConnector implements IDataProviderConnector {

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProviderConnector#reconnect(ss.framework.domainmodel2.AbstractDomainSpace)
	 */
	public void reconnect(AbstractDomainSpace space) {
		space.setDataProvider( SupraServerDataProviderFactory.INSTANCE.getServerDataProvider() );		
	}

}
