package ss.lab.dm3.connection.configuration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DomainPackageList implements Iterable<Package>{

	private Set<Package> items = new HashSet<Package>();

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Package> iterator() {
		return this.items.iterator();
	}

	public boolean add(Package item) {
		return this.items.add(item);
	}

	public Package[] toArray() {
		return this.items.toArray( new Package[ this.items.size() ]);
	}
	
	
	
}
