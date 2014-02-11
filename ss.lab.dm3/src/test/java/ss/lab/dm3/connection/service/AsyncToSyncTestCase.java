package ss.lab.dm3.connection.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;


import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.testsupport.AssertUtils;
import ss.lab.dm3.testsupport.service.TestService;
import ss.lab.dm3.testsupport.service.TestServiceAsync;

/**
 * TODO improve tests
 */
public class AsyncToSyncTestCase extends TestCase {

	public void test() {
		final List<String> messages = new ArrayList<String>();
	
		TestServiceAsync testServiceAsync = new TestServiceAsync() {
			public void getMessagesCount(ICallbackHandler resultHandler) {
				if ( resultHandler != null ) {
					resultHandler.onSuccess( messages.size() );
				}
			}
			public void sendMessage(String text, ICallbackHandler resultHandler) {
				messages.add( text );
				if ( resultHandler != null ) { 
					resultHandler.onSuccess(null);
				}
			}
		};
		final TestService testService = (TestService) AsyncToSyncProxy.create( testServiceAsync );
		testService.sendMessage( "Test message" );
		testService.sendMessage( "Another test message" );
		AssertUtils.assertSetSame( new Object[] {"Test message", "Another test message"}, messages );
	}
}
