/**
 * 
 */
package ss.common.threads;

import ss.common.ArgumentNullPointerException;
import ss.common.IdentityUtils;
import ss.framework.threads.LinearExecuteService;

/**
 *
 */
public final class SingleTheradExecutor<R extends Runnable> {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SingleTheradExecutor.class);
	
	private final String subName;
	
	private volatile LinearExecuteService service;	
	
	/**
	 * @param subName
	 */
	public SingleTheradExecutor(final String subName) {
		super();
		if ( subName == null ) {
			throw new ArgumentNullPointerException( "subName" );
		}
		this.subName = subName;
	}

	/**
	 * 
	 */
	public synchronized void start( String baseName ) {
		if ( isAlive() ) {
			throw new IllegalStateException( "Object already intitialized " + this );
		}
		this.service = new LinearExecuteService( IdentityUtils.getNextRuntimeIdForThread( baseName + this.subName ) );
	}

	public synchronized void beginExecute(R runnable) throws ObjectRefusedException {
		if ( !isAlive() ) {
			throw new ObjectRefusedException( runnable, "Single Thread Executor is dead" );
		}
		if ( !this.service.beginExecute( runnable ) ) {
			throw new ObjectRefusedException( runnable, "Object refused by " + this.service );
		}
	}
	
	public synchronized void shootdown() {
		if ( isAlive() ) {
			this.service.terminate();
			this.service = null;
		}
	}
	
	/**
	 * @return the isAlive
	 */
	public synchronized boolean isAlive() {
		return this.service != null;
	}

}
