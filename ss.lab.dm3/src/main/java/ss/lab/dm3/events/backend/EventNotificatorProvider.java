/**
 * 
 */
package ss.lab.dm3.events.backend;

import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.events.Category;
import ss.lab.dm3.events.EventListener;

/**
 * @author Dmitry Goncharov
 */
public interface EventNotificatorProvider {

	/**
	 * Returns null if notificator provider does not support this category now 
	 */
	<T extends EventListener> T getEventNotificator( Category<T> category );		
		
	BackEndContext getContext();
	
}
