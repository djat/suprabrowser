/**
 * 
 */
package ss.client.ui.browser.manager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.mozilla.interfaces.*;

/**
 * 
 */
public class NsObjectWrapper implements InvocationHandler {

	/**
	 * 
	 */
	private static final String RELEASE_METHOD_NAME = "release";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger.getLogger(NsObjectWrapper.class);
	
	private static final String QUERY_INTERFACE = "queryInterface";

	private final NsObjectInterfaces interfaces;
	
	private nsISupports impl;

	/**
	 * @param interfaces
	 * @param impl
	 */
	private NsObjectWrapper(NsObjectInterfaces interfaces, nsISupports impl) {
		super();
		if (impl == null) {
			throw new NullPointerException("impl");
		}
		if (interfaces == null) {
			throw new NullPointerException("interfaces");
		}
		this.interfaces = interfaces;
		this.impl = impl;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		final String methodName = method.getName();
		// Redirect call to return wrapper
		if ( methodName.equals( QUERY_INTERFACE ) ) {
			return queryInterface( (String)args[ 0 ] );
		}
		// Release object
		if ( method.getDeclaringClass().equals( NsWrapperBase.class )  ) {
			if ( methodName.equals( RELEASE_METHOD_NAME ) ) {
				release();
			}
			else {
				logger.error( "Unknown NsWrapperBase method " + method );
			}
			return null;
		}
		// Call original method
		return method.invoke( getCheckedImpl(), args );
	}
	
	/**
	 * 
	 */
	private void checkAlive() {
		if ( !isAlive() ) {
			throw new IllegalStateException( "Wrapper " + this + " is released" );
		}
	}

	/**
	 * @return
	 */
	private boolean isAlive() {
		return this.impl != null;
	}

	private void release() {
		if ( isAlive() ) {
			releasing();			
		}
	}

	private nsISupports getCheckedImpl() {
		checkAlive();
		return this.impl;
	}
	
	/**
	 * 
	 */
	private void releasing() {
		this.interfaces.release();
	}

	/**
	 * 
	 */
	nsISupports detach() {
		final nsISupports ret = this.impl;
		this.impl = null;
		return ret;
	}

	/**
	 * @param impl
	 * @return
	 */
	boolean isWrapperFor(nsISupports impl) {
		return this.impl == impl;
	}
	
	/**
	 * @param string
	 */
	private Object queryInterface(String uuid) {
		nsISupports impl = this.impl.queryInterface(uuid);
		return this.interfaces.getWrapper( impl );
	}

	/**
	 * @param interfaces
	 * @param impl2
	 * @return
	 */
	static NsWrapperBase createWrapper(
			NsObjectInterfaces interfaces, nsISupports impl) {
		final Class<?>[] implIntefaces = impl.getClass().getInterfaces();
		final Class<?>[] proxyInterfaces = new Class<?>[ implIntefaces.length + 1 ];
		proxyInterfaces[ implIntefaces.length ] = NsWrapperBase.class; 
		System.arraycopy( implIntefaces, 0, proxyInterfaces, 0, implIntefaces.length );
		return (NsWrapperBase) Proxy.newProxyInstance( impl.getClass().getClassLoader(),
			proxyInterfaces,
			new NsObjectWrapper( interfaces, impl) );
	}

	public static NsWrapperBase createWrapper( NsObjectFinalizer finalizer, nsISupports impl ) {
		return new NsObjectInterfaces( finalizer ).getWrapper( impl );
	}

	/**
	 * @param wrapperBase
	 * @return
	 */
	static NsObjectWrapper getWrapper(NsWrapperBase wrapperBase) {
		return (NsObjectWrapper) Proxy.getInvocationHandler(wrapperBase);
	}

	
}
