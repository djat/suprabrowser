/**
 * 
 */
package ss.lab.dm3.connection.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ss.lab.dm3.connection.service.proxy.ReflectionServiceInvokator;

/**
 * @author Dmitry Goncharov
 */
public class ServiceBackEndProvider {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());
	
	private final IServiceBackEndFactory serviceFactory;

	private final Hashtable<Class<?>, IServiceInvokator> serviceClassToInvokator = new Hashtable<Class<?>, IServiceInvokator>();
	
	private final List<ServiceBackEnd> services = new ArrayList<ServiceBackEnd>(); 
	
	private boolean disposed = false;
	
	/**
	 * @param serviceFactory
	 */
	public ServiceBackEndProvider(IServiceBackEndFactory serviceFactory) {
		super();
		this.serviceFactory = serviceFactory;
	}
	
	/**
	 * @param serviceClazz
	 * @return
	 */
	public synchronized IServiceInvokator getServiceInvokator(
			Class<? extends ServiceAsync> serviceClazz) {
		checkState();
		IServiceInvokator serviceInvokator = this.serviceClassToInvokator.get( serviceClazz );
		if ( serviceInvokator == null ) {
			ServiceBackEnd service = this.serviceFactory.create( serviceClazz );
			this.services.add(service);
			serviceInvokator = new ReflectionServiceInvokator( service );
			this.serviceClassToInvokator.put( serviceClazz, serviceInvokator );
		}
		return serviceInvokator;
	}
	
	/**
	 * 
	 */
	private synchronized void checkState() {
		if ( this.disposed ) {
			throw new IllegalStateException( this + " was disposed" );
		}
	}

	public void dispose() {
		if ( !this.disposed ) {
			this.disposed = true;
			diposing();
		}
	}

	/**
	 * 
	 */
	protected void diposing() {	
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Disposing " + this );
		}
		for( ServiceBackEnd service : this.services ) {
			service.dispose();
		}
		this.services.clear();
		this.serviceClassToInvokator.clear();
		this.serviceFactory.dispose();
	}
}
