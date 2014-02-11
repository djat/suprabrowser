package ss.lab.dm3.persist.search;

import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.persist.AbstractDomainTestCase;

public abstract class AbstractSearchTestCase extends AbstractDomainTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Recreate lucene index  
		BackEndContext systemBackEndContext = getSystemConnectionProvider().getBackEndContextProvider().getSystemBackEndContext();
		systemBackEndContext.getDataManagerBackEnd().searchReindex();
	}

	

}
