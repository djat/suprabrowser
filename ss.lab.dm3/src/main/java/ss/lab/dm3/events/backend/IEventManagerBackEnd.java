/**
 * 
 */
package ss.lab.dm3.events.backend;

import ss.lab.dm3.events.Category;
import ss.lab.dm3.events.EventListener;

/**
 *
 */
public interface IEventManagerBackEnd {

	<T extends EventListener> T getEventNotificator(Category<T> category);

	<T extends EventListener> T getEventNotificator(Class<T> eventClazz);

	void registerDispatcher(EventNotificatorProvider eventDispatcher);

	void unregisterDispatcher(EventNotificatorProvider eventDispatcher);

	<T extends EventListener> void addEventFilter(Category<T> category, Class<? extends AbstractNotificatorFilter<T>> filteredClazz );
		
}