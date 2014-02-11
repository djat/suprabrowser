package ss.lab.dm3.persist.pool;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainResolverHelper;
import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.persist.transaction.concurrent.MultiWaiter;
import ss.lab.dm3.persist.transaction.concurrent.RandomHelper;
import ss.lab.dm3.persist.transaction.concurrent.RunStatisticStamp;
import ss.lab.dm3.persist.workers.DomainWorkerHost;
import ss.lab.dm3.pool.DomainPool;
import ss.lab.dm3.testsupport.TestSystemConnectionProvider;
import ss.lab.dm3.testsupport.objects.UserAccount;

public class PoolTestCase extends TestCase implements DomainWorkerHost{

	private static final int BATCH_SIZE = 100;
	protected final Log log = LogFactory.getLog(getClass());
	
	public void test() {
		TestSystemConnectionProvider connectionProvider = TestSystemConnectionProvider.INSTANCE;
		connectionProvider.get();
		DomainPool pool = new DomainPool( connectionProvider );
		try {
 			for ( int n = 0; n < 100; n ++ ) {
 				RunStatisticStamp start = new RunStatisticStamp();
 				MultiWaiter multiWaiter = new MultiWaiter( BATCH_SIZE );
 	 			for ( int m = 0; m < BATCH_SIZE; m ++ ) {
 	 				new Task( pool, multiWaiter ).start();
 	 			}
 	 			multiWaiter.waitAll();
				System.out.println( "Batch done " + new RunStatisticStamp().minus( start ));
			}
		}
		finally {
			pool.dispose();
		}
	}

	private class Task implements Runnable {

		private final DomainPool pool;
		
		private final MultiWaiter multiWaiter;
		
		public Task(DomainPool pool, MultiWaiter multiWaiter) {
			super();
			this.pool = pool;
			this.multiWaiter = multiWaiter;
		}

		public void start() {
			new Thread( this ).start();
		}
		
		public void run() {
			Domain domain = pool.borrowDomain();
			try {
				domain.execute( new Runnable() {
					public void run() {
						doInDomain();
					}
				} );
			}
			finally {
				pool.returnDomain(domain);
				this.multiWaiter.onReady();
			}
		}

		protected void doInDomain() {
			final Domain domain = DomainResolverHelper.getCurrentDomain();
			final Transaction tx = domain.beginTrasaction();
			try {
				UserAccount account = domain.find( UserAccount.class, 
						new Long( RandomHelper.random( 1, 20 ) ) 
				);
				if ( account != null ) {
					account.setContactName( RandomHelper.randomName() );
				}
				tx.commit();
			}
			finally {
				tx.dispose();
			}
			
		}
		
	}
}
