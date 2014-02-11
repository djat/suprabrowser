package ss.lab.dm3.security2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Dmitry Goncharov
 */
public class AuthorityList implements Iterable<Authority> {

	private final Set<Authority> items = new HashSet<Authority>();
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Authority> iterator() {
		return this.items.iterator();
	}

	/**
	 * @param authority
	 * @return
	 */
	public boolean contains(Authority authority) {
		return this.items.contains( authority );
	}

	public void clear() {
		this.items.clear();
	}
	
	public void add(Authority authority) {
		this.items.add(authority);
	}

	/**
	 * @param userAuthorities
	 */
	public void set(Set<Authority> authorities) {
		this.clear();
		for( Authority authority : authorities ) {
			add( authority );
		}
	}

	/**
	 * @param authority
	 */
	public void remove(Authority authority) {
		this.items.remove( authority );
	}
	
}
