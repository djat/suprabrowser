/**
 * Jul 13, 2006 : 7:13:14 PM
 */
package ss.common.threads;

import org.apache.log4j.Logger;

import ss.global.SSLogger;

public class SyncGate {
	private Object sync = new Object();
	
	private static final Logger logger = SSLogger.getLogger(SyncGate.class);

	private boolean gateOpen = false;

	public boolean isGateOpen() {
		synchronized (this.sync) {
			return this.gateOpen;
		}
	}

	public void setGateOpen(boolean gateOpen) {
		synchronized (this.sync) {
			this.gateOpen = gateOpen;
			if (this.gateOpen) {
				this.sync.notifyAll();
			}
		}
	}

	public void waitForOpenGate() {
		synchronized (this.sync) {
			while (!this.gateOpen) {
				try {
					this.sync.wait();
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	public void waitForOpenGate(long mseconds) {
		synchronized (this.sync) {
			if (!this.gateOpen) {
				try {
					this.sync.wait(mseconds);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

}