package ss.lab.dm3.events.services;

import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.connection.service.ServiceAsync;
import ss.lab.dm3.events.Category;
import ss.lab.dm3.events.EventListener;

/**
 * @author Dmitry Goncharov
 */
public interface EventProviderAsync extends ServiceAsync {

	void unsubscribe( Category<? extends EventListener> category, ICallbackHandler handler );
	
	void subscribe( Category<? extends EventListener> category, ICallbackHandler handler );
	
	void fetchEvents( ICallbackHandler handler );
	
}
