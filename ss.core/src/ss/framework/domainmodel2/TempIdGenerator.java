/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.Random;

/**
 *
 */
public class TempIdGenerator {

	private final Random rand = new Random();
	
	private long counter = 1000;
	
	public synchronized long nextId() {
		return nextPsevdoId(); 
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unused")
	private long nextRuntimeId() {
		return (++ this.counter);
	}
	
	private long nextPsevdoId() {
		long id = this.rand.nextLong();
		return id > 0 ? id : -id;
	}
	
	
	
}
