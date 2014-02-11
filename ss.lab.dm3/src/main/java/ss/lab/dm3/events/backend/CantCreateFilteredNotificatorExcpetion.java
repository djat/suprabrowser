/**
 * 
 */
package ss.lab.dm3.events.backend;

import ss.lab.dm3.events.EventExcpetion;

/**
 * @author Dmitry Goncharov
 */
public class CantCreateFilteredNotificatorExcpetion extends EventExcpetion {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3945349380867519716L;


	/**
	 * @param filterClazz
	 * @param ex
	 */
	public CantCreateFilteredNotificatorExcpetion(
			Class<?> filterClazz,
			Throwable ex) {
		super( "Can't create filtered notificator by " + filterClazz, ex);
	}
}
