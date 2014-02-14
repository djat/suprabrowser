/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;

/**
 *
 */
public class LockedIterable<T> implements Iterable<T>  {

	private final Iterable<T> unsafe;
	
	private final Lock readLock;
	
	/**
	 * @param unsafe
	 * @param readLock
	 */
	public LockedIterable(final Iterable<T> unsafe, final Lock readLock) {
		super();
		this.unsafe = unsafe;
		this.readLock = readLock;
		this.readLock.lock();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return this.unsafe.iterator();
	}

	public void release() {
		this.readLock.unlock();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if ( this.unsafe instanceof Collection ) {
			return "LockedIterable based on collection size: " + ((Collection<T>)this.unsafe).size();
		}
		else {
			return "LockedIterable based on other iterable " + super.toString(); 
		}
	}
	
	
}
