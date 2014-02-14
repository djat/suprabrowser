/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
final class AliveDomainObjectCollectionList implements Iterable<DomainObjectCollection>{

	private volatile List<DomainObjectCollection> collections = new ArrayList<DomainObjectCollection>();
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public synchronized Iterator<DomainObjectCollection> iterator() {
		return this.collections.iterator();
	}

	/**
	 * @param collection
	 */
	public synchronized void add(DomainObjectCollection collection) {
		this.collections = new ArrayList<DomainObjectCollection>( this.collections );
		this.collections.add(collection);		
	}

	/**
	 * 
	 */
	public synchronized void clear() {
		this.collections = new ArrayList<DomainObjectCollection>();		
	}

}
