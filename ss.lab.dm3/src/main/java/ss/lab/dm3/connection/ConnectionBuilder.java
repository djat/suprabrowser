package ss.lab.dm3.connection;

import ss.lab.dm3.connection.configuration.IConfigurationProvider;
import ss.lab.dm3.connection.service.AbstractServiceProvider;
import ss.lab.dm3.connection.service.IServiceBackEndFactory;
import ss.lab.dm3.connection.service.ServiceBackEndFactory;
import ss.lab.dm3.connection.service.backend.BackEndContextProvider;
import ss.lab.dm3.connection.service.proxy.ProxyServiceProvider;
import ss.lab.dm3.persist.IDomainLockStrategy;
import ss.lab.dm3.persist.lock.multithread.MultithreadDomainLockStrategy;

/**
 * @author Dmitry Goncharov
 */
public class ConnectionBuilder {

	private final BackEndContextProvider backEndContextProvider;

	private IDomainLockStrategy domainLockStrategy = new MultithreadDomainLockStrategy();
	
	public ConnectionBuilder( IConfigurationProvider configurationProvider ) {
		this( new BackEndContextProvider( configurationProvider ) );
	}	
	
	/**
	 * @param backEndContextProvider
	 */
	public ConnectionBuilder(BackEndContextProvider backEndContextProvider) {
		super();
		this.backEndContextProvider = backEndContextProvider;
	}



	/**
	 * @return the domainLockStrategy
	 */
	public IDomainLockStrategy getDomainLockStrategy() {
		return this.domainLockStrategy;
	}

	/**
	 * @param domainLockStrategy the domainLockStrategy to set
	 */
	public void setDomainLockStrategy(IDomainLockStrategy domainLockStrategy) {
		this.domainLockStrategy = domainLockStrategy;
	}

	public Connection create(AbstractServiceProvider serviceProvider) {
		return new Connection( serviceProvider, this.domainLockStrategy );
	}
	
	public Connection createConnectionProxy( String accountName ) {
		IServiceBackEndFactory serviceBackEndFactory = createServiceBackEndFactory( accountName ); 
		return create( new ProxyServiceProvider( serviceBackEndFactory ) );
	}

	/**
	 * @param accountName
	 * @return
	 */
	public IServiceBackEndFactory createServiceBackEndFactory( String accountName) {
		return new ServiceBackEndFactory( this.backEndContextProvider.create( accountName ) );
	}

	public BackEndContextProvider getBackEndContextProvider() {
		return this.backEndContextProvider;
	}

	
}
