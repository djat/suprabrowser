package ss.lab.dm3.persist.transaction.concurrent;

import junit.framework.TestCase;
import ss.lab.dm3.connection.SystemConnectionProvider;
import ss.lab.dm3.testsupport.TestSystemConnectionProvider;

public class ExternalChanagesTestCase extends TestCase {
	
	public void test() {
		SystemConnectionProvider connectionProvider = TestSystemConnectionProvider.INSTANCE;
		WorkerPool pool = new WorkerPool( connectionProvider, 10 );
		for( int n = 0; n < 1000; ++ n ) {
			ConcurrentUnit concurrentUnit = new ConcurrentUnit( new Long( RandomHelper.random( 1, 19 ) ), 3 );
			concurrentUnit.execute( pool );
			concurrentUnit.waitAll();
		}
		
	}
}