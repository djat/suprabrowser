/**
 * 
 */
package ss.framework.threads;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import ss.common.IdentityUtils;

/**
 * 
 */
public abstract class LazyActionDispatcher<A> {

	/**
	 * 
	 */
	private static final int MAX_ACTION_COUNT = 2048;

	/**
	 * 
	 */
	private static final int MAX_FAIL_IN_ROW_COUNT = 3;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LazyActionDispatcher.class);

	private final static int UNREADY = 0;

	private final static int LAZY_INITIALIZING = 1;

	private final static int READY = 2;

	private final LinkedBlockingQueue<A> postponedActions = new LinkedBlockingQueue<A>( MAX_ACTION_COUNT );

	private volatile int initFailsInRow = 0;

	private volatile int state = UNREADY;

	private final ReentrantLock lock = new ReentrantLock();

	private final String name;

	/**
	 * @param name
	 */
	public LazyActionDispatcher(final String name) {
		super();
		this.name = IdentityUtils.getNextRuntimeId( name );
	}

	/**
	 * @param action
	 */
	public final boolean dispatch(A action) {
		this.lock.lock();
		try {
			if (getState() == READY) {
				dispatchImmediately(action);				
			} else {
				if (getState() == UNREADY
						&& this.initFailsInRow > MAX_FAIL_IN_ROW_COUNT) {
					return false;
				} else {
					this.postponedActions.offer(action);
					if (getState() == UNREADY) {
						setState(LAZY_INITIALIZING);
						beginInitialize();
					}
				}
			}
			return true;
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * @return
	 */
	public final String getName() {
		return this.name;
	}

	public final Future<Object> beginUninitialize() {
		FutureTask<Object> futureTask = new FutureTask<Object>( new Runnable() {
			public void run() {
				uninitialize();
			}
		}, null );
		LinearExecutors.beginExecute( getStateMutationKeyName(), futureTask );
		return futureTask;
	}

	/**
	 * 
	 */
	private final void beginInitialize() {
		LinearExecutors.beginExecute(getStateMutationKeyName(), new Runnable() {
			public void run() {
				initialize();
			}
		});
	}

	/**
	 * 
	 * @param action
	 */
	protected abstract void dispatchImmediately(A action);

	/**
	 * initializing and uninitializing executes in same thread.
	 */
	protected abstract void uninitializing();

	/**
	 * initializing and uninitializing executes in same thread
	 */
	protected abstract void intitializing() throws CantInitializeException;

	private void initialize() {
		if (getState() == READY) {
			logger.warn("Already inititalized " + this);
			return;
		}
		try {
			intitializing();			
		} catch (CantInitializeException ex) {
			// TODO Reporting errors
			logger.fatal("Can't initialize exception. Fails in row " + this.initFailsInRow, ex);
			this.lock.lock();
			try {
				this.initFailsInRow++;
			} finally {
				this.lock.unlock();
			}
			return;
		}
		completeInitialization();
		setState(READY);
	}
	
	/**
	 * @return the postponedActions
	 */
	public final boolean isStaying() {
		this.lock.lock();
		try {
			return this.postponedActions.size() == 0 && getState() == UNREADY;
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * @return
	 */
	private int getState() {
		this.lock.lock();
		try {
			return this.state;
		} finally {
			this.lock.unlock();
		}
	}

	private void setState(int newState) {
		this.lock.lock();
		try {
			this.state = newState;
		} finally {
			this.lock.unlock();
		}
	}

	private void uninitialize() {
		if (getState() != READY) {
			logger.warn("Already uninititalized " + this);
			return;
		}
		setState(UNREADY);
		// TODO think about postponedActions removing
		// this.postponedActions.clear();
		uninitializing();
	}

	private void completeInitialization() {
		for (;;) {
			final A action = this.postponedActions.poll();
			if (action != null) {
				dispatchImmediately(action);
			} else {
				this.lock.lock();
				try {
					this.initFailsInRow = 0;
					if (this.postponedActions.size() == 0) {
						setState(READY);
						return;
					}
				} finally {
					this.lock.unlock();
				}

			}
		}
	}

	/**
	 * @return
	 */
	private String getStateMutationKeyName() {
		return getName() + "-StateMutation";
	}

}
