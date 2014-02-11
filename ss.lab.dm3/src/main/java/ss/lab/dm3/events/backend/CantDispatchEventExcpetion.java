/**
 * 
 */
package ss.lab.dm3.events.backend;

import java.lang.reflect.Method;


import ss.lab.dm3.events.Category;
import ss.lab.dm3.events.EventExcpetion;
import ss.lab.dm3.events.EventListener;

/**
 * @author Dmitry Goncharov
 */
public class CantDispatchEventExcpetion extends EventExcpetion {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7858738915339202305L;


	/**
	 * @param category
	 * @param method
	 * @param filteredNotificator
	 * @param args
	 * @param ex
	 */
	public CantDispatchEventExcpetion(Category<?> category, Method method,
			EventListener filteredNotificator, Object[] args,
			Throwable ex) {
		super( "Can't dispatch event " + category + " : " + method + " for " + filteredNotificator + " with " + args, ex );
	}
}
