package ss.lab.dm3.persist.transaction.concurrent;

import org.junit.Assert;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.persist.TransactionConfclictedException;
import ss.lab.dm3.testsupport.objects.UserAccount;
import static d1.FastAccess.*;

public class ConcurrentTask extends AbstractTask {

	public ConcurrentTask(ConcurrentUnit unit) {
		super(unit);
	}

	@Override
	public void doWork() {
		Domain domain = getDomain();
		Transaction tx = domain.beginTrasaction();
		try {
			UserAccount account = getAccount();
			String randomName = RandomHelper.randomName();
			account.setContactName( randomName );
			if (log.isDebugEnabled()) {
				log.debug( $( "Concurent name {1} set upped by {0} ", this, randomName ) );
			}
			this.unit.onConcurrentReady();
			try {
				this.unit.waitMain();
				if (log.isDebugEnabled()) {
					log.debug( $("Try commit {1} by {0}", this, account.getContactName() ) );
				}	
				tx.commit();
				
				Assert.fail( "Conflict not found" );
			}
			catch( TransactionConfclictedException ex ) {
				if (log.isDebugEnabled()) {
					log.debug("Conflict found" );
				}
			}
		}
		finally {
			tx.dispose();
		}
	}

	

}
