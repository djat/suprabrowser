package ss.lab.dm3.persist.transaction.concurrent;

import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.testsupport.objects.UserAccount;

public class SuccessfulTask extends AbstractTask {

	public SuccessfulTask(ConcurrentUnit unit) {
		super(unit);
	}

	@Override
	public void doWork() {
		Transaction tx = getDomain().beginTrasaction();
		try {
			UserAccount account = super.getAccount();
			account.setContactName( this.unit.getSuccessfulName() );
			this.unit.waitConcurrents();
			if (log.isDebugEnabled()) {
				log.debug("All concurents done. Successful task begin " + this );
			}
			tx.commit();
		}
		finally {
			tx.dispose();
		}
		this.unit.onMainReady();
	}

	

}
