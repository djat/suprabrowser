/**
 * 
 */
package ss.lab.dm3.utils;

import java.lang.reflect.Method;

/**
 * @author Dmitry Goncharov
 */
public class CantNotifyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4874935950021377977L;

	/**
	 * @param item
	 * @param method
	 * @param ex
	 */
	public CantNotifyException(Object item, Method method,
			Throwable ex) {
		super( "Can't invoke " + item + ", by " + method, ex );
	}
}
