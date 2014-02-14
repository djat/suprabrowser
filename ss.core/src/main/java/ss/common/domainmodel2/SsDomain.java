/**
 * 
 */
package ss.common.domainmodel2;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.DomainChangesListener;
import ss.framework.domainmodel2.EditingScope;
import ss.framework.domainmodel2.IDataProviderConnector;
import ss.framework.domainmodel2.IDataProviderConnectorResolver;
import ss.framework.domainmodel2.Transaction;

/**
 * 
 */
public final class SsDomain implements IDataProviderConnectorResolver {

	private final static SsDomain INSTANCE = new SsDomain();

	public final static SsDomainSpace SPACE = new SsDomainSpace( INSTANCE );	
	
	public final static MemberHelper MEMBER_HELPER = SPACE.getHelper(MemberHelper.class);

	public final static SphereHelper SPHERE_HELPER = SPACE.getHelper(SphereHelper.class);

	public final static InvitedMemberHelper INVITED_MEMBER_HELPER = SPACE.getHelper(InvitedMemberHelper.class);
	
	public final static ConfigurationHelper CONFIGURATION = SPACE.getHelper(ConfigurationHelper.class );

	private IDataProviderConnector dataProviderConnector = null;
	
	private SsDomain() {}

	/**
	 * @return
	 */
	public static Transaction createTransaction() {
		return SPACE.createTransaction();
	}

	/**
	 * @return
	 */
	public static EditingScope createEditingScope() {
		return SPACE.createEditingScope();
	}
	
	/**
	 * @param listener
	 * 
	 */
	public static void addDomainChangesListener(DomainChangesListener listener) {
		SPACE.addDomainChangesListener(listener);
	}

	/**
	 * @param listener
	 */
	public static void removeDomainChangesListener(
			DomainChangesListener listener) {
		SPACE.removeDomainChangesListener(listener);
	}
	
	/**
	 * Only for unit tests/debug purpose
	 */
	public static void debug_forseReconnect() {
		SPACE.reconnectDataProvider();
	}
	
	/**
	 * Only for unit tests/debug purpose.
	 */
	public static void debug_resetCache() {
		SPACE.resetCache();
	}

	/**
	 * 
	 */
	public static void initialize( IDataProviderConnector dataProviderConnector ) {
		if ( dataProviderConnector == null ) {
			throw new ArgumentNullPointerException( "dataProviderConnector" );
		}
		INSTANCE.dataProviderConnector = dataProviderConnector;			
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProviderConnectorResolver#getDataProviderConnector()
	 */
	public IDataProviderConnector getDataProviderConnector() {
		if ( this.dataProviderConnector == null ) {
			throw new IllegalStateException( "dataProviderConnector is not setupped. Please set up it, by calling SsDomain.initialize, before SsDomain usage." );
		}
		return this.dataProviderConnector;
	}
	
	public static void addChangesListener(DomainChangesListener listener) { 
		SPACE.addDomainChangesListener(listener);
	}

}
