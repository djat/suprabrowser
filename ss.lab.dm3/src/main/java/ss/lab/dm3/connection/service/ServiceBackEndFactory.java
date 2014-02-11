package ss.lab.dm3.connection.service;

import ss.lab.dm3.connection.service.backend.BackEndContext;

/**
 * @author Dmitry Goncharov
 */
public class ServiceBackEndFactory implements IServiceBackEndFactory {

	private final BackEndContext context;
	
	private boolean disposed = false;
	
	/**
	 * TODO add resolver extending
	 *  
	 * @param context
	 */
	public ServiceBackEndFactory(BackEndContext context) {
		super();
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.connection.service.IServiceFactory#create(java.lang.Class)
	 */
	public ServiceBackEnd create(Class<? extends ServiceAsync> serviceAsyncClass) {
		Class<? extends ServiceBackEnd> backEndClass = ServiceUtils.getBackEndByAsync(serviceAsyncClass);
		try {
			ServiceBackEnd serviceBackEnd = backEndClass.newInstance();
			serviceBackEnd.initialize(this.context);
			return serviceBackEnd;
		} catch (InstantiationException ex) {
			throw new CantCreateServiceException(backEndClass, ex);
		} catch (IllegalAccessException ex) {
			throw new CantCreateServiceException(backEndClass, ex);
		}
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.connection.service.IServiceBackEndFactory#dispose()
	 */
	public synchronized void dispose() {
		if ( !this.disposed ) {
			this.disposed = true;
			this.context.dispose();
		}
	}

}
