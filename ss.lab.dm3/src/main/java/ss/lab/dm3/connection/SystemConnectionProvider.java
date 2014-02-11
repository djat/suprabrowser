package ss.lab.dm3.connection;

import ss.lab.dm3.connection.configuration.IConfigurationProvider;
import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.connection.service.backend.BackEndContextProvider;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainLoader;
import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.persist.script.builtin.StartupLoaderScript;
import ss.lab.dm3.security2.backend.ISecurityManagerBackEnd;

/**
 * @author Dmitry Goncharov
 */
public class SystemConnectionProvider {

	private final BackEndContextProvider backEndContextProvider;
	
	private Connection systemConnection = null;
	
	public SystemConnectionProvider( IConfigurationProvider configurationProvider ) {
		 this.backEndContextProvider = new BackEndContextProvider( configurationProvider );
	}
	
	public IConfigurationProvider getConfigurationProvider() {
		return this.backEndContextProvider.getConfigurationProvider();
	}

	public void setConfigurationProvider(
			IConfigurationProvider configurationProvider) {
		this.backEndContextProvider
				.setConfigurationProvider(configurationProvider);
	}
	
	/**
	 * @param accountName
	 * @return
	 * @see ss.lab.dm3.connection.service.backend.BackEndContextProvider#create(java.lang.String)
	 */
	public BackEndContext createBackEndContext(String accountName) {
		return this.backEndContextProvider.create(accountName);
	}

	/**
	 * TODO think about connection pooling 
	 * @return
	 */
	public synchronized Connection get() {
		if ( this.systemConnection == null ) {
			ConnectionBuilder connectionBuilder = createConnectionBuilder(); 
			this.systemConnection = connectionBuilder.createConnectionProxy( ISecurityManagerBackEnd.SYSTEM_ACCOUNT_NAME );
		}
		return this.systemConnection;
	}

	public Connection create() {
		ConnectionBuilder connectionBuilder = createConnectionBuilder(); 
		return connectionBuilder.createConnectionProxy( "default" );
	}
	
	/**
	 * @return
	 */
	public ConnectionBuilder createConnectionBuilder() {
		ConnectionBuilder connectionBuilder = new ConnectionBuilder( this.backEndContextProvider );
		return connectionBuilder;
	}

	public BackEndContextProvider getBackEndContextProvider() {
		return this.backEndContextProvider;
	}
	
	/**
	 * 
	 */
	public synchronized void resetDomain() {
		if ( this.systemConnection != null ) {
			final Domain domain = this.systemConnection.getDomain();
			domain.execute( new Runnable() {
				public void run() {
					Transaction tx = domain.getTransaction();
					if ( tx != null ) {
						tx.rollback();
					}
					domain.getRepository().unloadAll();
					// Load default data set
					DomainLoader dataLoader = new DomainLoader( new StartupLoaderScript() );
					CallbackResultWaiter initialLoaderWaiter = new CallbackResultWaiter();
					dataLoader.beginLoad( domain, initialLoaderWaiter );
					initialLoaderWaiter.waitToResult();					
				}
			});
		}
	}


	
}
