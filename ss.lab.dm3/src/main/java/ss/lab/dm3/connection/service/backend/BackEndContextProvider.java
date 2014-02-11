/**
 * 
 */
package ss.lab.dm3.connection.service.backend;

import ss.lab.dm3.connection.configuration.IConfigurationProvider;
import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.backend.ISecurityManagerBackEnd;

/**
 * @author Dmitry Goncharov
 */
public class BackEndContextProvider {

	private IConfigurationProvider configurationProvider = null;
	
	private volatile BackEndFeatures backEndFeatures = null;
	
	private volatile BackEndContext systemBackEndContext = null;
	
	public BackEndContextProvider() {
	}
	
	public BackEndContextProvider( IConfigurationProvider configurationProvider) {
		super();
		this.configurationProvider = configurationProvider;
	}
	
	public BackEndContextProvider( BackEndContext systemBackEndContext ) {
		this.systemBackEndContext = systemBackEndContext;
	}
	
	synchronized BackEndFeatures getBackEndFeatures() {
		if ( this.backEndFeatures == null ) {
			if ( this.configurationProvider == null ) {
				throw new IllegalStateException( "Can't create system backend context, because configuration provider is null" );
			}
			this.backEndFeatures = new BackEndFeatures( this.configurationProvider.get() );
			this.backEndFeatures.initialize();
		}
		return this.backEndFeatures;
	}
	
	public synchronized BackEndContext getSystemBackEndContext() {
		if ( this.systemBackEndContext == null ) {
			
//			public static BackEndContext createBackEndContext( BackEndFeatures backEndFeatures, String accountName ) {
//				Authentication authentication = backEndContextProvider.trustedAuthenticate( accountName  );
//				return new BackEndContext( backEndContextProvider, authentication );
//			}
			this.systemBackEndContext = create( ISecurityManagerBackEnd.SYSTEM_ACCOUNT_NAME );
		}
		return this.systemBackEndContext;
	}
	
	
	/**
	 * @return the configurationProvider
	 */
	public synchronized IConfigurationProvider getConfigurationProvider() {
		return this.configurationProvider;
	}

	/**
	 * @param configurationProvider the configurationProvider to set
	 */
	public synchronized void setConfigurationProvider(IConfigurationProvider configurationProvider) {
		if ( this.systemBackEndContext != null ) {
			throw new IllegalStateException( "Can't change configuration provider because system backend context already initialized." );
		}
		this.configurationProvider = configurationProvider;
	}

	public BackEndContext create( String accountName ) {
		// TODG check following code 
//		BackEndContext systemContext = getSystemBackEndContext();
//		Authentication authentication = systemContext.getSecurityManagerBackEnd().trustedAuthenticate( accountName );
//		if ( authentication == systemContext.getAuthentication() ) {
//			return systemContext;
//		}
//		else {
			Authentication authentication = this.trustedAuthenticate(accountName);
			return new BackEndContext( getBackEndFeatures(), authentication );		
//		}
	}

	/**
	 * @param accountName
	 * @return
	 */
	public Authentication trustedAuthenticate(String accountName) {
		final ISecurityManagerBackEnd securityManagerBackEnd = getBackEndFeatures().getSecurityManagerBackEnd();
		return securityManagerBackEnd.trustedAuthenticate(accountName);
	}
	
}
