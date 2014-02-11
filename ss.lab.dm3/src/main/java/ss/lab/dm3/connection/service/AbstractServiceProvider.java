package ss.lab.dm3.connection.service;

import java.util.Hashtable;

import ss.lab.dm3.utils.ListenerList;

/**
 * @author Dmitry Goncharov
 */
public abstract class AbstractServiceProvider {

	private final ListenerList<ServiceProviderListener> listeners = ListenerList.create( ServiceProviderListener.class );

	private final Hashtable<Class<?>, ServiceAsync> clazzToServiceAsync = new Hashtable<Class<?>, ServiceAsync>();

	private boolean disposed = false;
	/**
	 * @param serviceFactory
	 */
	public AbstractServiceProvider() {
		super();
	}

	public final void addServiceProviderListener(ServiceProviderListener listener) {
		this.listeners.add(listener);
	}

	public final void removeServiceProviderListener(
			ServiceProviderListener listener) {
		this.listeners.remove(listener);
	}

	protected void notifyDisposing() {
		this.listeners.getNotificator().disposing();
	}

	protected abstract <T extends ServiceAsync> IServiceInvokator createServiceInvokator(
			Class<T> serviceAsyncClass);

	protected <T extends ServiceAsync> T create(Class<T> serviceAsyncClass) {
		IServiceInvokator invokator = createServiceInvokator(serviceAsyncClass);
		return ServiceAsyncProxy.create(serviceAsyncClass, invokator);
	}

	public synchronized final void dispose() {
		if ( !this.disposed ) {
			this.disposed = true; 
			disposing();
		}
	}

	/**
	 * 
	 */
	protected void disposing() {
		notifyDisposing();
		this.clazzToServiceAsync.clear();
		this.listeners.clear();
	}

	/**
	 * @param class
	 * @return
	 */
	public synchronized <T extends ServiceAsync> T getAsyncService(
			Class<T> serviceAsyncClass) {
		ServiceAsync service = this.clazzToServiceAsync.get(serviceAsyncClass);
		if (service == null) {
			service = this.create(serviceAsyncClass);
			this.clazzToServiceAsync.put(serviceAsyncClass, service);
		}
		return serviceAsyncClass.cast(service);
	}

	/**
	 * TODG add caching
	 * @param class1
	 * @return
	 */
	public synchronized <T extends Service> T getSyncProxyService(Class<T> serviceClass) {
		Class<? extends ServiceAsync> asyncClazz = ServiceUtils.getAsyncBySync(serviceClass);
		ServiceAsync async = getAsyncService(asyncClazz);
		return serviceClass.cast( AsyncToSyncProxy.create( async, asyncClazz ) );
	}
	
	/**
	 * @return
	 */
	public synchronized boolean isDisposed() {
		return this.disposed;
	}


}
