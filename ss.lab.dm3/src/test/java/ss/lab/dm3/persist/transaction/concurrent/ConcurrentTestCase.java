package ss.lab.dm3.persist.transaction.concurrent;

import junit.framework.TestCase;
import ss.lab.dm3.connection.SystemConnectionProvider;
import ss.lab.dm3.testsupport.TestSystemConnectionProvider;

public class ConcurrentTestCase extends TestCase {
	
	public void test() {	
		SystemConnectionProvider connectionProvider = TestSystemConnectionProvider.INSTANCE;
		connectionProvider.get();
		
		RunStatisticStamp start = new RunStatisticStamp();
		WorkerPool pool = new WorkerPool( connectionProvider, 10 );
		for( int n = 0; n < 100; ++ n ) {
			ConcurrentUnit concurrentUnit = new ConcurrentUnit( new Long( RandomHelper.random( 1, 20 ) ), 5 );
			concurrentUnit.execute( pool );
			concurrentUnit.waitAll();
		}
		System.out.println( new RunStatisticStamp().minus( start ).toString() );
	}
	
}
