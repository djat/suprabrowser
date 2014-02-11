package ss.lab.dm3.connection.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author dmitry
 *
 */
public class InternalDataClassList implements Iterable<Class<?>> {

	private List<Class<?>> items = new ArrayList<Class<?>>();
	
	
	public void add( Class<?> dataClazz ) {
		this.items.add( dataClazz );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Class<?>> iterator() {
		return this.items.iterator();
	}
	
	/**
	 * @return
	 */
	public Class<?>[] toArray() {
		return this.items.toArray( new Class<?>[ this.items.size() ] );
	}

	/**
	 * @param collection 
	 * 
	 */
	public void addAll(Collection<Class<?>> collection) {
		this.items.addAll( collection );
	}
}
