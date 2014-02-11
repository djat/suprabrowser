/**
 * 
 */
package ss.lab.dm3.persist.workers;

import java.lang.reflect.Method;

/**
 * @author Dmitry Goncharov
 */
public class CantInvokeMethodException extends DomainWorkerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5011118364166708928L;

	/**
	 * @param method
	 */
	public CantInvokeMethodException(Method method) {
		super( "Invalid method signature " + method.getName() );
	}

	/**
	 * @param method
	 * @param paramenterClazzes
	 */
	public CantInvokeMethodException(Method method, Class<?> expectedClazz, Class<?> actualClazz) {
		super( "Invalid method parameter type. Expected " + expectedClazz + ", actual " + actualClazz );
	}

	/**
	 * @param method
	 * @param ex
	 */
	public CantInvokeMethodException(Method method, Throwable ex) {
		super( "Method invokation failed " + method, ex );
	}
	
}
