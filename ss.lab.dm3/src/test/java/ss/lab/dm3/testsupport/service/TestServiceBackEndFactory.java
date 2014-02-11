package ss.lab.dm3.testsupport.service;

import ss.lab.dm3.connection.service.IServiceBackEndFactory;
import ss.lab.dm3.connection.service.ServiceAsync;
import ss.lab.dm3.connection.service.ServiceBackEnd;


/**
 * @author Dmitry Goncharov
 *
 */

public final class TestServiceBackEndFactory implements IServiceBackEndFactory {
	
	private final TestServiceBackEnd testBackEnd = new TestServiceBackEnd();

	public ServiceBackEnd create(Class<? extends ServiceAsync> serviceAsyncClass) {
		if ( serviceAsyncClass == TestServiceAsync.class ) {
			return this.testBackEnd;
		}
		else {
			throw new IllegalArgumentException( "Unexpected async service class " + serviceAsyncClass );
		}
	}

	public void dispose() {}

	public TestServiceBackEnd getTestBackEnd() {
		return this.testBackEnd;
	}

	
}