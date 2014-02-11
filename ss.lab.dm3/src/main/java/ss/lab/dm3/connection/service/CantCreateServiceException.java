package ss.lab.dm3.connection.service;

/**
 * @author Dmitry Goncharov
 *
 */
public class CantCreateServiceException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8082616244942811896L;

	/**
	 * @param implClassName
	 * @param ex
	 */
	public CantCreateServiceException(String implClassName, ClassNotFoundException ex) {
		super( "Can't create service by class name " + implClassName, ex ); 
	}

	/**
	 * @param ex
	 */
	public CantCreateServiceException(Class<?> serviceClass, Throwable ex) {
		super( "Can't create service " + serviceClass, ex );
	}
	
	

}
