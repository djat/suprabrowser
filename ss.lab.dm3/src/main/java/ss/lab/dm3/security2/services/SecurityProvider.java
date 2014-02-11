package ss.lab.dm3.security2.services;

import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.connection.service.Service;
import ss.lab.dm3.security2.Authentication;

public interface SecurityProvider extends Service {

	Authentication getAuthentication() throws ServiceException;
	
}
