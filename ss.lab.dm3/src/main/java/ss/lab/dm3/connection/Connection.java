package ss.lab.dm3.connection;

import java.util.concurrent.atomic.AtomicBoolean;

import ss.lab.dm3.connection.service.AbstractServiceProvider;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.connection.service.ServiceProviderAdaptor;
import ss.lab.dm3.connection.service.ServiceAsync;
import ss.lab.dm3.events.EventManager;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.IDomainLockStrategy;
import ss.lab.dm3.security2.SecurityManager;
import ss.lab.dm3.utils.ListenerList;

/**
 * @author Dmitry Goncharov
 */
public class Connection {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private AbstractServiceProvider serviceProvider;

	private SecurityManager securityManager;

	private EventManager eventManager;

	private Domain domain;

	private final ListenerList<ConnectionListener> connectionListeners = ListenerList.create(ConnectionListener.class);
	
	private final IDomainLockStrategy domainLockStrategy;
	
	private final AtomicBoolean closed = new AtomicBoolean( false );

	private final ServiceProviderAdaptor serviceProviderListener = new ServiceProviderObserver();

	/**
	 * @param remoteServiceAsyncFactory
	 * 
	 */
	public Connection(AbstractServiceProvider serviceProvider, IDomainLockStrategy domainLockStrategy)
			throws ServiceException {
		super();
		this.serviceProvider = serviceProvider;
		this.domainLockStrategy = domainLockStrategy;
		// First start event manager
		this.eventManager = new EventManager(this.serviceProvider);
		// Second security manager
		this.securityManager = new SecurityManager(this.serviceProvider);
		// Last one is domain
		this.domain = new Domain( this.serviceProvider, this.eventManager, this.securityManager, this.domainLockStrategy );
		this.serviceProvider.addServiceProviderListener(this.serviceProviderListener);
	}

	public <T extends ServiceAsync> T getAsyncService(Class<T> serviceClass) {
		
		return this.serviceProvider.getAsyncService(serviceClass);
	}

	public void close() {
		if ( this.closed.compareAndSet( false, true ) ) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Begin connection closing " + this );
			}
			this.serviceProvider.removeServiceProviderListener(this.serviceProviderListener);
			this.serviceProvider.dispose();
			this.connectionListeners.getNotificator().beforeConnectionClosed(this);
			this.connectionListeners.clear();
			if (this.log.isDebugEnabled()) {
				this.log.debug("Closing connection " + this );
			}
			if ( this.domain != null ) {
				this.domain.execute( new Runnable() {
					public void run() {
						Connection.this.domainLockStrategy.uninstall();
					}
				});
			}
			if (this.log.isDebugEnabled()) {
				this.log.debug("End connection closing " + this );
			}
		}
	}

	/**
	 * @param listener
	 */
	public void addConnectionListener(ConnectionListener listener) {
		this.connectionListeners.add(listener);
	}

	public Domain getDomain() {
		return this.domain;
	}

	public EventManager getEventManager() {
		return this.eventManager;
	}

	public SecurityManager getSecurityManager() {
		return this.securityManager;
	}

	/**
	 * @return
	 */
	public boolean isDisposed() {
		return this.serviceProvider.isDisposed();
	}

	private final class ServiceProviderObserver extends ServiceProviderAdaptor {
		@Override
		public void disposing() {
			super.disposing();
			close();
		}
	}
}
