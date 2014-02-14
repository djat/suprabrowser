package ss.common.domainmodel2;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.IDataProvider;
import ss.framework.domainmodel2.IDataProviderConnector;
import ss.framework.domainmodel2.IDataProviderConnectorResolver;

final public class SsDomainSpace extends AbstractDomainSpace {

	private final IDataProviderConnectorResolver dataProviderConnectorResolver;
	
	public SsDomainSpace( IDataProviderConnectorResolver dataProviderConnectorResolver ) {
		if ( dataProviderConnectorResolver == null ) {
			throw new ArgumentNullPointerException( "dataProviderConnectorResolver" );
		}
		this.dataProviderConnectorResolver = dataProviderConnectorResolver;
		registerHelper( MemberHelper.class );
		registerHelper( SphereHelper.class );
		registerHelper( InvitedMemberHelper.class );
		registerHelper( ConfigurationHelper.class );
	}

	@Override
	protected void reconnectDataProvider() {
		IDataProviderConnector connector = this.dataProviderConnectorResolver.getDataProviderConnector();
		connector.reconnect(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.domainmodel2.AbstractDomainSpace#beforeChangeDataProvider(ss.framework.domainmodel2.IDataProvider)
	 */
	@Override
	protected void beforeChangeDataProvider(IDataProvider oldDataProvider) {
		super.beforeChangeDataProvider(oldDataProvider);
		oldDataProvider.dispose();
	}
}