/**
 * 
 */
package ss.lab.dm3;

import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.connection.service.backend.BackEndContextProvider;
import ss.lab.dm3.events.backend.IEventManagerBackEnd;
import ss.lab.dm3.persist.backend.IDataManagerBackEnd;
import ss.lab.dm3.testsupport.TestConfigurationProvider;

/**
 *
 */
public class MockHelper {

	private final static BackEndContextProvider CONTEXT_PROVIDER = new BackEndContextProvider( TestConfigurationProvider.INSTANCE );
	
	/**
	 * @return
	 */
	public static IEventManagerBackEnd getEventManagerBackEnd() {
		return getSystemBackEndContext().getEventManagerBackEnd();
	}

	/**
	 * @return
	 */
	private static BackEndContext getSystemBackEndContext() {
		return CONTEXT_PROVIDER.getSystemBackEndContext();
	}

	/**
	 * @return
	 */
	public static IDataManagerBackEnd getDataManagerBackEnd() {
		return getSystemBackEndContext().getDataManagerBackEnd();
	}

}
