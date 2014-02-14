/**
 * 
 */
package ss.framework.threads;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 */
public class LinearExecutors {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LinearExecutors.class);
	
	/**
	 * Singleton instance
	 */
	public final static LinearExecutors INSTANCE = new LinearExecutors();

	private LinearExecutors() {
	}
	
	private final ReentrantLock lock = new ReentrantLock();

	private final Map<String, LinearExecuteService> lineNameToService = new Hashtable<String, LinearExecuteService>();

	public static boolean beginExecute(String lineName, Runnable runnable) {
		return INSTANCE.beginExecuteImpl(lineName, runnable);
	}
		
	private boolean beginExecuteImpl(String lineName, Runnable runnable) {
		this.lock.lock();
		try {
			LinearExecuteService service = this.lineNameToService.get( lineName );
			if ( service == null ) {
				service = new LinearExecuteService( lineName );
				this.lineNameToService.put(lineName, service);
			}
			boolean ret = service.beginExecute(runnable);
			if ( !ret ) {
				logger.error( "Task rejected " + runnable + " by " + service );
			}
			removeStayingServices();
			return ret;
		} finally {
			this.lock.unlock();
		}
	}

	
	/**
	 * 
	 */
	private void removeStayingServices() {
		this.lock.lock();
		try {
			ArrayList<LinearExecuteService> serviceToRemove = null;
			for (LinearExecuteService service : this.lineNameToService.values()) {
				if (!service.hasWorker()) {
					if (serviceToRemove == null) {
						serviceToRemove = new ArrayList<LinearExecuteService>();
					}
					serviceToRemove.add(service);
				}
			}
			if (serviceToRemove != null) {
				for (LinearExecuteService service : serviceToRemove) {
					this.lineNameToService.remove(service.getName());
				}
			}
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * @param first_line
	 */
	public boolean hasLine(String lineName) {
		this.lock.lock();
		try {
			removeStayingServices();
			return this.lineNameToService.containsKey(lineName);
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * @param first_line
	 */
	public int getLineTasksCount(String lineName) {
		this.lock.lock();
		try {
			removeStayingServices();
			LinearExecuteService service = this.lineNameToService.get(lineName);
			return service != null ? service.getTasksCount() : 0; 
		} finally {
			this.lock.unlock();
		}
	}
	

}
