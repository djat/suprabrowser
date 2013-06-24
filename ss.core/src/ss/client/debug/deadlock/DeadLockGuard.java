package ss.client.debug.deadlock;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import ss.common.IdentityUtils;
import ss.common.debug.DebugUtils;
import ss.framework.domainmodel2.ObjectDisposedException;

public class DeadLockGuard {

	/**
	 * 
	 */
	public static final int DEFAULT_IDLE_TIME_THRESHOLD = 60000; // 1 min
	
	public static final int DEAD_LOCK_CHECKER_SLEEP_TIME = 3000; 
	
	private final int hangUpThreshold;
	
	private final AtomicLong lastMessagesDispatchedTimeStamp = new AtomicLong( 0 );

	private final Thread checkerThread = new Thread( new DeadLockChecker() );
	
	private final AtomicBoolean disposed = new AtomicBoolean();  
	
	private final IDeadlockMessagesListener deadlockMessagesListener;
	
	/**
	 * 
	 */
	public DeadLockGuard( IDeadlockMessagesListener deadlockMessagesListener, int hangUpThreshold ) {
		super();
		this.hangUpThreshold = hangUpThreshold;
		this.checkerThread.setDaemon( true );
		this.checkerThread.setName( IdentityUtils.getNextRuntimeIdForThread( DeadLockChecker.class ) );
		this.deadlockMessagesListener = deadlockMessagesListener;
	}
	
	public DeadLockGuard( IDeadlockMessagesListener deadlockMessagesListener ) {
		this( deadlockMessagesListener, DEFAULT_IDLE_TIME_THRESHOLD );
	}
	

	public synchronized void start() {
		if ( isDisposed() ) {
			throw new ObjectDisposedException( this );
		}
		notifyAlive();	 
		this.checkerThread.start(); 
	}

	public final void notifyAlive() {
		this.lastMessagesDispatchedTimeStamp.set( new Date().getTime() );
	}
	
	
	private class DeadLockChecker implements Runnable {
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				DeadLockGuard.this.deadlockMessagesListener.started();
				while( !isDisposed() ) {
					dumpThreadsIfNeeded();
					try {
						Thread.sleep( DEAD_LOCK_CHECKER_SLEEP_TIME );
					} catch (InterruptedException ex) {
						dispose();
					}
				}
			}
			finally {
				DeadLockGuard.this.deadlockMessagesListener.ended();
			}	
		}		
	}
	
	private void dumpThreadsIfNeeded() {
		long lastTimeStamp = this.lastMessagesDispatchedTimeStamp.get();
		long nowTimeStamp = new Date().getTime();
		long idleTime = nowTimeStamp - lastTimeStamp;
		if ( idleTime > this.hangUpThreshold ) {
			dumpAllActiveThreadsStack();
			dispose();
		}
	}

	/**
	 * 
	 */
	private void dumpAllActiveThreadsStack() {
		this.deadlockMessagesListener.dumpPossibleDeadlock( DebugUtils.dumpAllThreads( "Possible deadlock" ) );
	}

	public final void dispose() {
		if ( this.disposed.compareAndSet( false, true ) ) {
			disposing();
		}		
	}

	/**
	 * 
	 */
	protected void disposing() {
		this.checkerThread.interrupt();
	}
	
	/**
	 * @return
	 */
	public final boolean isDisposed() {
		return this.disposed.get();
	}

}
