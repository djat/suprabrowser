/**
 * 
 */
package ss.framework.threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import ss.common.IdentityUtils;

/**
 * 
 */
public final class LinearExecuteService {
	/**
	 * 
	 */
	private static final int MAX_TASK_COUNT = 1024;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LinearExecuteService.class);

	private final String name;

	private final LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>(
			MAX_TASK_COUNT);

	private volatile Worker worker = null;

	private volatile boolean terminated = false;

	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * @param manager
	 * @param name
	 */
	public LinearExecuteService(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean beginExecute(Runnable runnable) {
		this.lock.lock();
		try {
			if (isTerminated()) {
				return false;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("beginExecute " + runnable);
			}
			final boolean taskAdded = this.tasks.offer(runnable);
			startWorkerIfRequired();
			return taskAdded;
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * @return
	 */
	public boolean hasWorker() {
		this.lock.lock();
		try {
			return this.worker != null;
		} finally {
			this.lock.unlock();
		}
	}

	public void terminate() {
		this.lock.lock();
		try {
			this.terminated = true;
			// TODO notification about removed tasks
			this.tasks.clear();
			if (this.worker != null) {
				this.worker.interrupt();
			}
			this.worker = null;
		} finally {
			this.lock.unlock();
		}
	}

	private void workDone(Worker worker) {
		this.lock.lock();
		try {
			if (this.worker == worker) {
				if (logger.isDebugEnabled()) {
					logger.debug("Finish worker " + this.worker);
				}
				this.worker = null;
			}
			startWorkerIfRequired();
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * @param worker
	 * @return
	 */
	private Runnable pollTask(Worker worker) {
		if (logger.isDebugEnabled()) {
			logger.debug("Before polling for " + worker);
		}
		this.lock.lock();
		try {
			if (this.worker != worker || isTerminated()) {
				return null;
			}
			Runnable ret = this.tasks.poll();
			// TODO add time out
			if (logger.isDebugEnabled()) {
				logger.debug("Polled " + ret);
			}
			return ret;
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * @param ex
	 */
	private void handleException(Runnable task, Throwable ex) {
		// TODO Auto-generated method stub
		logger.error("Task failed " + task, ex);
	}

	/**
	 * 
	 */
	public boolean isTerminated() {
		this.lock.lock();
		try {
			return this.terminated;
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * 
	 */
	private void startWorkerIfRequired() {
		this.lock.lock();
		try {
			if (this.tasks.size() > 0 && !hasWorker() && !isTerminated()) {
				this.worker = new Worker( getName() );
				this.worker.start();
				if (logger.isDebugEnabled()) {
					logger.debug("Start worker " + this.worker);
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Don't start worker. Current worker is "
							+ this.worker);
				}
			}
		} finally {
			this.lock.unlock();
		}
	}

	public int getTasksCount() {
		this.lock.lock();
		try {
			int count = this.tasks.size();
			if ( hasWorker() ) {
				++ count; 
			}
			return count;
		} finally {
			this.lock.unlock();
		}
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getName() + " is terminated " + this.isTerminated() + ", tasks count " + this.tasks.size() + " of " + MAX_TASK_COUNT;
	}
	
	/**
	 * 
	 */
	private final class Worker implements Runnable {

		private final Thread thread;

		private final String name;
		/**
		 * 
		 */
		public Worker( String baseName ) {
			super();
			this.thread = new Thread(this);
			this.name = IdentityUtils.getNextRuntimeId( baseName + "-worker" );
			this.thread.setName( this.name );
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				Runnable task;
				while ((task = pollTask(this)) != null) {
					try {
						if (isTerminated()) {
							return;
						}
						if (logger.isDebugEnabled()) {
							logger.debug("Running " + task);
						}
						// Clean up interrupted flag
						Thread.interrupted();
						task.run();
					} catch (Throwable ex) {
						handleException(task, ex);
					}
				}
			} finally {
				if (logger.isDebugEnabled()) {
					logger.debug("Going to done " + this);
				}
				workDone(this);
			}
		}

		/**
		 * 
		 */
		public void start() {
			this.thread.start();
		}

		/**
		 * 
		 */
		public void interrupt() {
			this.thread.interrupt();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.name;
		}
		
	}
	
	

}
