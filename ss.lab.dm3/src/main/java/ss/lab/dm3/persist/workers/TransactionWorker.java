package ss.lab.dm3.persist.workers;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.Transaction;

/**
 * @author Dmitry Goncharov
 */
public abstract class TransactionWorker<T extends DomainWorkerContext> extends DomainWorker<T> {

	/* (non-Javadoc)
	 * @see ss.lab.dm3.transactiontest.DomainWorker#run(ss.lab.dm3.transactiontest.DomainWorkerContext)
	 */
	@Override
	public final Object run(T context) {
		final Domain domain = context.getDomain();
		final Transaction tx = domain.beginTrasaction();
		try {
			runInTransaction(context);
		}
		catch( Throwable ex ) {
			tx.rollback();
			throw new DomainWorkerException( ex );
		}
		return tx;
	}

	protected abstract void runInTransaction(DomainWorkerContext context);
	
}

