package ss.lab.dm3.testsupport.service;

import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.connection.service.ServiceAsync;

public interface TestServiceAsync extends ServiceAsync {
	
	void sendMessage( String text, ICallbackHandler resultHandler);
	
	void getMessagesCount(ICallbackHandler resultHandler);	

}
