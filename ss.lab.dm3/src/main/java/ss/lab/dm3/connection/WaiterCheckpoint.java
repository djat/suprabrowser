package ss.lab.dm3.connection;

import ss.lab.dm3.utils.DebugUtils;

public class WaiterCheckpoint {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private boolean handled;

	public WaiterCheckpoint() {
		super();
		if (this.log.isDebugEnabled()) {
			this.log.debug("Created " + this );
		}
	}

	public synchronized void pass() {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Passed " + this + " " + DebugUtils.getCurrentStackTrace() );
		}
		this.handled = true;
		notifyAll();
	}
	
	public synchronized boolean isPassed() {
		return this.handled;
	}
	
	
}
