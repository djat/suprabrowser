package ss.lab.dm3.connection.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.ICallbackHandler;

/**
 * 
 * @author Dmitry Goncharov
 * 
 * TODO add method cache 
 *
 */
public class AsyncToSyncProxy implements InvocationHandler {

	private final ServiceAsync serviceAsync;
	
	/**
	 * @param serviceAsync
	 */
	private AsyncToSyncProxy(ServiceAsync serviceAsync) {
		super();
		this.serviceAsync = serviceAsync;
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// Async args has additional arg - callbackHandler, so add it  
		Object[] asyncArgs = new Object[ args.length + 1 ];
		// Copy original args
		System.arraycopy( args, 0, asyncArgs, 0, args.length);
		// Create waiter and set it as last arg
		CallbackResultWaiter waiter = new CallbackResultWaiter();
		asyncArgs[ args.length ] = waiter;
		Method asyncMethod = getAsyncMethod( method );
		asyncMethod.invoke( this.serviceAsync, asyncArgs);
		return waiter.waitToResult();
	}

	/**
	 * @param method
	 * @return
	 */
	private Method getAsyncMethod(Method method) {
		final Class<?>[] parameters = method.getParameterTypes();
		final Class<?>[] asyncParameters = new Class<?>[ parameters.length + 1 ];
		System.arraycopy( parameters, 0, asyncParameters, 0, parameters.length);
		asyncParameters[ parameters.length ] = ICallbackHandler.class;  
		try {
			return this.serviceAsync.getClass().getMethod( method.getName(), asyncParameters );
		}
		catch (SecurityException ex) {
			throw new ServiceException("Can't access to method " + method.getName(),
					ex);
		} catch (NoSuchMethodException ex) {
			throw new ServiceException("Can't find method " + method.getName(), ex);
		}
	}
	
	public static Service create( ServiceAsync async ) {
		final Class<? extends ServiceAsync> asyncClazz = ServiceUtils.getServiceClazz( ServiceAsync.class, async );
		return create(async, asyncClazz);
	}

	/**
	 * @param async
	 * @param asyncClazz
	 * @return
	 */
	public static Service create(ServiceAsync async, Class<? extends ServiceAsync> asyncClazz) {
		if ( !asyncClazz.isInstance(async) ) {
			throw new IllegalArgumentException( async + " is not instance of " + asyncClazz );
		}
		final Class<? extends Service> syncClazz = ServiceUtils.getSyncByAsync( asyncClazz );
		final Object rawProxy = Proxy.newProxyInstance( async.getClass().getClassLoader(), new Class<?> [] {
				syncClazz
			}, new AsyncToSyncProxy( async ) );
		return syncClazz.cast( rawProxy );
	}

}
