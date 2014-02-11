package ss.lab.dm3.persist.transaction.concurrent;


import ss.lab.dm3.connection.CallbackHandler;
import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.Connection;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainResolverHelper;
import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.persist.workers.DomainWorkerHelper;
import ss.lab.dm3.persist.workers.DomainWorkerHost;
import ss.lab.dm3.persist.workers.SimpleDomainWorkerRunner;
import ss.lab.dm3.testsupport.TestSystemConnectionProvider;
import ss.lab.dm3.testsupport.objects.UserAccount;
import junit.framework.TestCase;


public class RejectTransactionTestCase extends TestCase {
	
	
	public void test() {
		Connection first = TestSystemConnectionProvider.INSTANCE.create();
		Connection second = TestSystemConnectionProvider.INSTANCE.create();
		try {
			CallbackResultWaiter waiter = new CallbackResultWaiter();
			Editor original = new Editor( waiter, null );
			Editor concurent = new Editor( null, waiter );
			DomainWorkerHelper.run( second.getDomain(), concurent, "edit" );
			DomainWorkerHelper.run( first.getDomain(), original, "edit" );
			original.waitToEnd();
			concurent.waitToEnd();
			try {
				Thread.sleep( 1000 );
			} catch (InterruptedException ex) {
			}
		}
		finally {
			first.close();
			second.close();
		}		
	}

	public class Editor implements DomainWorkerHost {
		
		final CallbackHandler handler;
		final CallbackResultWaiter waiter;
		final CallbackResultWaiter endWaiter = new CallbackResultWaiter();
		
		public Editor(CallbackHandler handler, CallbackResultWaiter waiter) {
			super();
			this.handler = handler;
			this.waiter = waiter;
		}

		public void edit() {
			Domain domain = DomainResolverHelper.getCurrentDomain();
			Transaction tx = domain.beginTrasaction();
			try {
				UserAccount userAccount = domain.find( UserAccount.class, 1L );
				String newName = String.valueOf( hashCode() );
				if ( waiter != null ) {
					System.out.println( "Current contact name is " + userAccount.getContactName() );
				}
				userAccount.setContactName( newName );
				if ( waiter != null ) {
					waiter.waitToResult();
				}
				Sleeper.sleep( 1000 );
				if ( waiter != null ) {
					System.out.println( "After wait contact name is " + userAccount.getContactName() );
				}
				System.out.println( "Commiting " + newName );
				tx.commit();
				if ( handler != null ) {
					handler.onSuccess( null );
				}
			}
			finally {
				tx.dispose();
				endWaiter.onSuccess( null );
			}
		}
		
		public void waitToEnd() {
			endWaiter.waitToResult();
		}
	}

}
