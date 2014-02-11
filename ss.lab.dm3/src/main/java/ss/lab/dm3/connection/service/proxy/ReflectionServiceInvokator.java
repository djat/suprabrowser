package ss.lab.dm3.connection.service.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.connection.service.IServiceInvokator;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.connection.service.Service;

/**
 * @author Dmitry Goncharov
 */
public class ReflectionServiceInvokator implements IServiceInvokator {

	private final Service impl;

	/**
	 * @param impl
	 */
	public ReflectionServiceInvokator(Service impl) {
		super();
		this.impl = impl;
	}

	public void ainvoke(String methodName, Class<?>[] parameterTypes,
			Serializable[] args, final ICallbackHandler resultHandler) throws ServiceException {
		final Object[] objArgs;
		if (args != null) {
			objArgs = new Object[args.length];
			System.arraycopy(args, 0, objArgs, 0, objArgs.length);
		} else {
			objArgs = null;
		}
		// Get method
		final Method implMethod = findMethod(methodName, parameterTypes);
		// Call implementation
		
		//  Thread thread = new Thread( new Runnable() {
		//	public void run() {
		
				try {
					final Object objResult = implMethod.invoke(ReflectionServiceInvokator.this.impl, objArgs);
					if ( resultHandler != null ) {
						resultHandler.onSuccess( objResult );
					}
				} catch (IllegalArgumentException ex) {
					throw new ServiceException("Call " + implMethod + " failed.", ex);
				} catch (IllegalAccessException ex) {
					throw new ServiceException("Call " + implMethod + " failed.", ex);
				} catch (InvocationTargetException ex) {
					Throwable targetEx = ex.getTargetException();
					if ( targetEx == null ) {
						targetEx = ex;
					}
					resultHandler.onFail( targetEx );
				}
				
		//	}
		//});
		//thread.start();
				
	}

	/**
	 * @param methodName
	 * @param parameterTypes
	 * @param implMethod
	 * @return
	 */
	private Method findMethod(String methodName, Class<?>[] parameterTypes) {
		try {
			return this.impl.getClass().getMethod(methodName,
					parameterTypes);
		} catch (SecurityException ex) {
			throw new ServiceException("Can't access to method " + methodName,
					ex);
		} catch (NoSuchMethodException ex) {
			throw new ServiceException("Can't find method " + methodName, ex);
		}
	}

}
