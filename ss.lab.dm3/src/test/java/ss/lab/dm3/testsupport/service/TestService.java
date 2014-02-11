package ss.lab.dm3.testsupport.service;

import ss.lab.dm3.connection.service.Service;

public interface TestService extends Service {
	
	void sendMessage( String text );
	
	int getMessagesCount();	

}
