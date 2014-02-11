package ss.lab.dm3.events.services;

import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.connection.service.Service;
import ss.lab.dm3.events.Category;
import ss.lab.dm3.events.EventList;

/**
 * @author Dmitry Goncharov
 */
public interface EventProvider extends Service {

	void unsubscribe( Category<?> category) throws ServiceException;
	
	void subscribe( Category<?> category ) throws ServiceException;
	
	EventList fetchEvents() throws ServiceException;
	
}
