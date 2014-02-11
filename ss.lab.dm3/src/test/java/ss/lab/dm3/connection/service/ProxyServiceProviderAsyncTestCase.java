package ss.lab.dm3.connection.service;

import java.util.List;

import ss.lab.dm3.connection.service.proxy.ProxyServiceProvider;
import ss.lab.dm3.testsupport.AssertUtils;
import ss.lab.dm3.testsupport.service.TestServiceAsync;
import ss.lab.dm3.testsupport.service.TestServiceBackEndFactory;
import junit.framework.TestCase;

public class ProxyServiceProviderAsyncTestCase extends TestCase {

	public void test() {
		final TestServiceBackEndFactory serviceImplementationFactory = new TestServiceBackEndFactory();
		ProxyServiceProvider provider = new ProxyServiceProvider( serviceImplementationFactory );
		TestServiceAsync testServiceAsync = provider.getAsyncService( TestServiceAsync.class );
		testServiceAsync.sendMessage( "Hello, ", null );
		testServiceAsync.sendMessage( "World!", null );
		final List<String> messages = serviceImplementationFactory.getTestBackEnd().getMessages();
		AssertUtils.assertSetSame( new Object[] { "Hello, ", "World!" }, messages);		
	}

	
}
