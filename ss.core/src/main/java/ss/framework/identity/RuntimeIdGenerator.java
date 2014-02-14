/**
 * 
 */
package ss.framework.identity;

/**
 *
 */
public class RuntimeIdGenerator {

	/**
	 * 
	 */
	public static final int MAX_VALUE = Integer.MAX_VALUE / 2;
	
	private final int maxValue;
	
	private volatile int counter = 0;

	/**
	 * 
	 */
	public RuntimeIdGenerator() {
		this.maxValue = MAX_VALUE;
	}

	public synchronized int nextId() {
		++ this.counter;
		this.counter %= this.maxValue;
		return this.counter;
	}

}