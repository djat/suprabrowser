/**
 * 
 */
package ss.lab.dm3.connection.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ss.lab.dm3.persist.DomainObject;

/**
 *
 */
public class DomainDataClassList implements Iterable<Class<? extends DomainObject>>{

	private List<Class<? extends DomainObject>> items = new ArrayList<Class<? extends DomainObject>>();
	
	public void add( Class<? extends DomainObject> dataClazz ) {
		this.items.add( dataClazz );
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Class<? extends DomainObject>> iterator() {
		return this.items.iterator();
	}

	/**
	 * @return
	 */
	public Class<?>[] toArray() {
		return this.items.toArray( new Class<?>[ this.items.size() ] );
	}

	/**
	 * 
	 */
	public void addAll( Collection<Class<? extends DomainObject>> collection ) {
		this.items.addAll( collection );
	}
	
	
}
