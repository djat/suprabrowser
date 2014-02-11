package ss.lab.dm3.persist.lock.multithread;

import ss.lab.dm3.connection.Waiter;
import ss.lab.dm3.connection.WaiterCheckpoint;
import ss.lab.dm3.persist.Domain;

public class MultithreadDomainWaiter extends Waiter {

	public static final long INF_TIMEOUT = 3 * 60000;
	
	private final Domain domain;
	
	/**
	 * @param domain
	 */
	public MultithreadDomainWaiter(Domain domain) {
		super();
		this.domain = domain;
	}

	@Override
	public void await(WaiterCheckpoint checkpoint, long timeout) {
		this.domain.unlock();
		try {
			super.await(checkpoint, timeout);
		}
		finally {
			this.domain.tryLock( INF_TIMEOUT );
		}
	}

	
}
