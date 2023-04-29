/**
 * 
 */
package ss.common.threads;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import ss.common.DateUtils;

/**
 *
 */
public final class ThreadBlocker {


	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ThreadBlocker.class);
	
	private final long timeout;
	
	private final long startupTime;
	
	private final AtomicBoolean released = new AtomicBoolean( false );   
	

	/**
	 * @param timeout
	 */
	public ThreadBlocker(int duration, TimeUnit unit ) {
		this( unit.toMillis(duration) );
	}
	
	/**
	 * @param timeout
	 */
	public ThreadBlocker(long timeout ) {
		super();
		/*if ( this.timeout < 0 ) {
			throw new IllegalArgumentException( "timeout" );
		}*/
		this.timeout = timeout;
		this.startupTime = System.currentTimeMillis();
	}
	
	/**
	 * @return
	 */
	public synchronized void blockUntilRelease() throws TimeOutException {
		long waitTime = getRemainWaitTime();		
		while( waitTime > 0 && 
			   !isReleased() ) {
			try {
				if ( logger.isDebugEnabled() ) {
					logger.debug( "Wait reply: " + waitTime );
				}				
				wait( waitTime );
			} catch (InterruptedException ex) {
				// Do nothing. We leave only when wait time comes to 0 or condition comes true.
			}
			waitTime = getRemainWaitTime();
		}
		if ( !isReleased() ) {
			throw new TimeOutException( this.timeout, this.startupTime );
		}
	}

	/**
	 * @return
	 */
	private long getRemainWaitTime() {
		return Math.max( this.startupTime + this.timeout - System.currentTimeMillis(), 0 );
	}

	/**
	 * @return
	 */
	public boolean isReleased() {
		return this.released.get();
	}

	/**
	 * 
	 */
	public synchronized boolean release() {
		boolean alreadyReleased = this.released.getAndSet( true );
		notifyAll();
		return !alreadyReleased;
	}
	
	/**
	 *
	 */
	public static class TimeOutException extends RuntimeException {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6458807582165361955L;
		
		private final long timeout;
		
		private final long startupTime;
		
		/**
		 * @param timeout
		 * @param startupTime
		 */
		public TimeOutException(long timeout, long startupTime) {
			super( "Thread block timeout. Startup time " +  DateUtils.dateToCanonicalString( startupTime ) + ", timeout " + timeout  );
			this.timeout = timeout;
			this.startupTime = startupTime;
		}
		
		public long getStartupTime() {
			return this.startupTime;
		}
		
		public long getTimeout() {
			return this.timeout;
		}
		
	}
}
