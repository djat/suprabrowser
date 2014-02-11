package ss.lab.dm3.connection.service;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import ss.lab.dm3.connection.ICallbackHandler;

/**
 * @author Dmitry Goncharov
 */
public class ServiceAsyncProxy implements InvocationHandler {

	private final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());

	private final IServiceInvokator serviceInvokator;

	private final Class<? extends ServiceAsync> servedClass;

	/**
	 * @param impl
	 */
	private ServiceAsyncProxy(Class<? extends ServiceAsync> servedClass, IServiceInvokator serviceInvokator) {
		super();
		this.servedClass = servedClass;
		this.serviceInvokator = serviceInvokator;
	}

	/**
	 * @return the log
	 */
	public org.apache.commons.logging.Log getLog() {
		return this.log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(final Object proxy, final Method asyncMethod,
			final Object[] asyncArgs) throws Throwable {
		if ( asyncMethod.getDeclaringClass() == IServiceProxy.class ) {
			if ( asyncMethod.getName() == "getServedClass" ) {
				return this.servedClass;
			}
			else {
				throw new UnsupportedOperationException( "Can't invoke " + asyncMethod );
			}
		}
		else {
			callInvokator(asyncMethod, asyncArgs);
			// All RemoteServiceFrontEndAsync should have void return type,
			// so returns null
			return null;
		}
	}

	/**
	 * @param asyncMethod
	 * @param asyncArgs
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void callInvokator(final Method asyncMethod,
			final Object[] asyncArgs) throws Throwable {
		// Zero : gets handler
		final ICallbackHandler handler = (ICallbackHandler) asyncArgs[asyncArgs.length - 1];
		if (handler == null) {
			// TODO: default handler
		}
		try {
			Serializable[] args;
			Class<?>[] parameterTypes;
			if (asyncArgs.length > 1) {
				// First gets argument for method implementation resolving
				final Class<?>[] asyncParameterTypes = asyncMethod.getParameterTypes();
				parameterTypes = new Class<?>[asyncParameterTypes.length - 1];
				System.arraycopy(asyncParameterTypes, 0, parameterTypes, 0,
					parameterTypes.length);
				// Assemble arguments for implementation call
				args = new Serializable[asyncArgs.length - 1];
				System.arraycopy(asyncArgs, 0, args, 0, args.length);
			} else {
				parameterTypes = null;
				args = null;
			}
			this.serviceInvokator.ainvoke( asyncMethod.getName(), parameterTypes, args, handler );
		} catch (Throwable ex) {
			if (handler != null) {
				getLog().warn(
					"Method invokation failed " + asyncMethod + ", Exception "
							+ ex );
				handler.onFail(ex);
			}
			return;
		}
	}

	public static <T extends ServiceAsync> T create(Class<T> asyncService,
			IServiceInvokator serviceInvokator) {
		Object proxy = Proxy.newProxyInstance(asyncService.getClassLoader(),
			new Class<?>[] { asyncService, IServiceProxy.class }, new ServiceAsyncProxy( asyncService, 
				serviceInvokator));
		return asyncService.cast(proxy);
	}

}
