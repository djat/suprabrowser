package ss.lab.dm3.connection.service;

/**
 * @author Dmitry Goncharov
 */
public interface IServiceAsyncFactory {

	<T extends ServiceAsync> T create(Class<T> serviceAsyncClass);

	void addServiceFactoryListener(ServiceProviderListener listener);

	void removeServiceFactoryListener(ServiceProviderListener listener);
}