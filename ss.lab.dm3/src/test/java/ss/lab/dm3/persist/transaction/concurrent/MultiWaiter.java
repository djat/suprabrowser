package ss.lab.dm3.persist.transaction.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

import ss.lab.dm3.connection.CallbackResultWaiter;

public class MultiWaiter {
	
	private final AtomicInteger count;
	private final CallbackResultWaiter waiter = new CallbackResultWaiter();	
	
	public MultiWaiter(int count) {
		super();
		this.count = new AtomicInteger(count);
	}

	public void onReady() {
		int result = count.decrementAndGet();
		if ( result < 0 ) {
			throw new IllegalStateException( "More than expected onReady calls for " + this );
		}
		if ( count.get() <= 0 ) { 
			this.waiter.onSuccess( null );
		}
	}

	public void waitAll() {
		this.waiter.waitToResult( 40000 );
	}

}
