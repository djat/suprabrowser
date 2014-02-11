package ss.lab.dm3.connection.service;

/**
 * Takes async service interface and creates ServiceBackEnd that 
 * implements sync service variant
 * 
 * @author Dmitry Goncharov
 */
public interface IServiceBackEndFactory {

	ServiceBackEnd create(Class<? extends ServiceAsync> serviceAsyncClass);

	void dispose();
}