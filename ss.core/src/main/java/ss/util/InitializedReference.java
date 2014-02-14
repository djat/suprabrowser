/**
 * 
 */
package ss.util;

import org.apache.log4j.Logger;

import ss.global.SSLogger;

/**
 * @author dankosedin
 * 
 */
public class InitializedReference<Type> {

	private static Logger logger = SSLogger
			.getLogger(InitializedReference.class);

	private boolean initialized;

	private Type value;

	public InitializedReference() {
		this.value = null;
		this.initialized = false;
	}

	public boolean isInitialized() {
		synchronized (this) {
			return this.initialized;
		}
	}

	public void initialize(Type value) {
		synchronized (this) {
			if (!this.initialized) {
				this.value = value;
				this.initialized = true;
				this.notifyAll();
			}
		}
	}

	public Type detachValue() {
		synchronized (this) {
			//TODO: running task interruption
			Type ret = this.value;
			this.value = null;
			this.initialized = false;
			return ret;
		}
	}
	
	public Type getValue() {
		synchronized (this) {
			if (this.initialized) {
				return this.value;
			} else {
				try {
					this.wait(60000);
				} catch (InterruptedException ex) {
					logger
							.error("IE exeption while waiting initialization",
									ex);
				}
				if (this.initialized) {
					return this.value;
				} else {
					throw new RuntimeException("Reference was not initialized");
				}
			}
		}		
	}

}
