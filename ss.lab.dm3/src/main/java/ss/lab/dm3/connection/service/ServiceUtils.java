package ss.lab.dm3.connection.service;

public class ServiceUtils {

	public static final String BACK_END_SUFIX = "BackEnd";

	public static final String SERVICE_ASYNC_SUFIX = "Async";
	
	public static Class<? extends ServiceBackEnd> getBackEndByAsync( Class<? extends ServiceAsync> async ) {
		String serviceName = getSyncName(async);
		String backEndServiceName = serviceName + BACK_END_SUFIX;
		try {
			Class<?> backEndClass = Class.forName(backEndServiceName);
			return backEndClass.asSubclass( ServiceBackEnd.class );
		}
		catch (ClassNotFoundException ex) {
			throw new ServiceException( "Can't find backend for " + async + ", lookup name is " + backEndServiceName, ex );
		}
	}

	/**
	 * @param async
	 * @return
	 */
	private static String getSyncName(Class<? extends ServiceAsync> async) {
		String asyncServiceName = async.getName();
		if (!asyncServiceName.endsWith(SERVICE_ASYNC_SUFIX)) {
			throw new IllegalArgumentException("Async service "
					+ async + " should have name that ends with "
					+ SERVICE_ASYNC_SUFIX);
		}
		String serviceName = asyncServiceName.substring(0,
			asyncServiceName.length() - SERVICE_ASYNC_SUFIX.length());
		return serviceName;
	}
	
	public static Class<? extends ServiceAsync> getAsyncBySync( Class<? extends Service> sync ) {
		String syncServiceName = sync.getName();
		String serviceName = syncServiceName + SERVICE_ASYNC_SUFIX;
		try {
			Class<?> asyncServiceClass = Class.forName(serviceName );
			return asyncServiceClass.asSubclass( ServiceAsync.class );
		}
		catch (ClassNotFoundException ex) {
			throw new ServiceException( "Can't find async service for " + sync + ", lookup name is " + serviceName, ex );
		}		
	}

	/**
	 * @param async 
	 * @param async
	 */
	public static <T> Class<? extends T> getServiceClazz(Class<T> expectedSuper, Object service) {
		if ( expectedSuper == null ) {
			throw new NullPointerException( "expectedSuper" );
		}
		Class<?> raw;
		if ( service instanceof IServiceProxy ) {
			raw = ((IServiceProxy) service).getServedClass();
		}
		else {
			raw = service.getClass();
			if ( !raw.isInterface() ) {
				for( Class<?> candidate : raw.getInterfaces() ) {
					if ( expectedSuper.isAssignableFrom( candidate ) ) {
						raw = candidate;
						break;
					}
				}
			}
		}
		return raw.asSubclass( expectedSuper );
	}

	/**
	 * @param asyncClazz
	 * @return
	 */
	public static Class<? extends Service> getSyncByAsync(Class<? extends ServiceAsync> asyncClazz) {
		final String syncName = getSyncName(asyncClazz);
		try {
			Class<?> serviceClass = Class.forName(syncName );
			return serviceClass.asSubclass( Service.class );
		}
		catch (ClassNotFoundException ex) {
			throw new ServiceException( "Can't find async service for " + asyncClazz + ", lookup name is " + syncName, ex );
		}		
	}

}
