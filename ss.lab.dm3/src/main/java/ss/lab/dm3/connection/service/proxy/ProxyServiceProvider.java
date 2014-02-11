package ss.lab.dm3.connection.service.proxy;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.connection.service.AbstractServiceProvider;
import ss.lab.dm3.connection.service.IServiceBackEndFactory;
import ss.lab.dm3.connection.service.IServiceInvokator;
import ss.lab.dm3.connection.service.ServiceAsync;
import ss.lab.dm3.connection.service.ServiceBackEnd;

/**
 * @author Dmitry Goncharov
 */
public class ProxyServiceProvider extends AbstractServiceProvider {

	private final List<ServiceBackEnd> services = new ArrayList<ServiceBackEnd>(); 
	
	private final IServiceBackEndFactory serviceImplementationFactory;

	/**
	 * @param serviceImplementationFactory
	 */
	public ProxyServiceProvider(IServiceBackEndFactory serviceImplementationFactory) {
		super();
		this.serviceImplementationFactory = serviceImplementationFactory;
	}

	/**
	 * @param <T>
	 * @param serviceAsyncClass
	 * @return
	 */
	@Override
	protected <T extends ServiceAsync> IServiceInvokator createServiceInvokator(
			Class<T> serviceAsyncClass) {
		final ServiceBackEnd serviceImpl = this.serviceImplementationFactory
				.create(serviceAsyncClass);
		this.services.add(serviceImpl); 
		return new ReflectionServiceInvokator(serviceImpl);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.connection.service.AbstractServiceAsyncProvider#disposing()
	 */
	@Override
	protected void disposing() {
		super.disposing();
		for( ServiceBackEnd service : this.services ) {
			service.dispose();
		}
		this.services.clear();
		this.serviceImplementationFactory.dispose();
	}

}
