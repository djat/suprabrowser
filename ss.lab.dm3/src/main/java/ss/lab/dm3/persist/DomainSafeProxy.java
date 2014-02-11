package ss.lab.dm3.persist;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import ss.lab.dm3.connection.ICallbackHandler;

/**
 * @author Dmitry Goncharov
 */
public class DomainSafeProxy implements InvocationHandler {

	private	final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Domain domain;
	
	private final Object impl;
	
	
	/**
	 * @param domain
	 * @param impl
	 */
	private DomainSafeProxy(Domain domain, Object impl) {
		super();
		this.domain = domain;
		this.impl = impl;
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args)
			throws Throwable {
		return this.domain.execute( new Callable<Object>() {
			public Object call() throws Exception {
				try {
					return method.invoke( DomainSafeProxy.this.impl, args);
				} catch (Throwable ex) {
					// Try to redirect to ICallbackHandler.onFail
					// ICallbackHandler should be last arg, so try find it and call 
					if ( args.length > 0 ) {
						Object lastArg = args[ args.length - 1 ];
						if ( lastArg instanceof ICallbackHandler ) {
							final ICallbackHandler callbackHandler = (ICallbackHandler) lastArg;
							if (getLog().isDebugEnabled()) {
								getLog().debug( "Redirecting fail to " + callbackHandler );
							}
							callbackHandler.onFail( ex );
							return null;
						}
					}
					getLog().error( "Failed to execute " + method, ex );
					return null;
				} 
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static <T> T create( Domain domain, T impl, Class<T> interfaze ) {
		if ( Proxy.isProxyClass( impl.getClass() ) ) {
			if ( Proxy.getInvocationHandler( impl ) instanceof DomainSafeProxy ) {
				return impl;
			}
		}
		return (T) create(domain, impl, new Class<?>[] { interfaze } );		
	}
	
	public static Object create( Domain domain, Object impl, Class<?>[] interfaces ) {
		return Proxy.newProxyInstance( impl.getClass().getClassLoader(), interfaces, new DomainSafeProxy( domain, impl ) );
	}

	/**
	 * @return the log
	 */
	public org.apache.commons.logging.Log getLog() {
		return this.log;
	}
}
