/**
 * 
 */
package ss.lab.dm3.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Dmitry Goncharov
 */
public class SimpleRuntimeIdGenerator {

	private final AtomicLong count = new AtomicLong();
	
	private final String qualifier;
	
	/**
	 * @param qualifier
	 */
	public SimpleRuntimeIdGenerator(String qualifier) {
		super();
		this.qualifier = qualifier;
	}

	/**
	 * @return
	 */
	public Long nextId() {
		return this.count.incrementAndGet();
	}

	/**
	 * @return
	 */
	public String qualifiedNextId() {
		return this.qualifier + "#" + String.valueOf( nextId() );
	}
}
