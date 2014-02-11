package ss.lab.dm3.testsupport;

import ss.lab.dm3.connection.SystemConnectionProvider;

public class TestSystemConnectionProvider extends SystemConnectionProvider {

	public static final TestSystemConnectionProvider INSTANCE = new TestSystemConnectionProvider();

	/**
	 * @param configurationProvider
	 */
	public TestSystemConnectionProvider() {
		super( TestConfigurationProvider.INSTANCE );
	}
	
}
