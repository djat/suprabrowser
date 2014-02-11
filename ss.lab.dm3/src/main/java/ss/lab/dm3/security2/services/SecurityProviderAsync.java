package ss.lab.dm3.security2.services;

import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.connection.service.ServiceAsync;

public interface SecurityProviderAsync extends ServiceAsync {

	void getAuthentication(ICallbackHandler callbackHandler);
	
}

