package ss.lab.dm3.persist.lock.multithread;

import ss.lab.dm3.connection.Waiter;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainLockStrategy;

public class MultithreadDomainLockStrategy extends DomainLockStrategy {

	public static final int DEFAULT_TIME_OUT = 60000;

	@Override
	public void executeFromNotDomainThread(Runnable runnable) {
		// TODO rewrite tryLock functionality
		final Domain domain = checkAndGetDomain();
		if (!domain.tryLock(DEFAULT_TIME_OUT)) {
			throw new IllegalStateException("Can't run " + runnable + ", because athother thread lock domain for more than " + DEFAULT_TIME_OUT );
		}
		try {
			runnable.run();
		} finally {
			domain.unlock();
		}	
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.IDomainLockStrategy#createWaiter()
	 */
	@Override
	public Waiter createWaiter() {
		return new MultithreadDomainWaiter( checkAndGetDomain() );
	}
}
