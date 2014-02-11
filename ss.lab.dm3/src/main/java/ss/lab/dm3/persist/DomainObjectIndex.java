package ss.lab.dm3.persist;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class DomainObjectIndex<T extends DomainObject>{

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass()); 
	
	private final Class<T> itemClass;
	
	private final Set<T> items = new HashSet<T>();

	/**
	 * @param itemClass
	 */
	public DomainObjectIndex(Class<T> itemClass) {
		super();
		this.itemClass = itemClass;
	}

	void collect( DomainObjectCollector<T> collector ) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Collect from " + this );
		}
		QueryMatcher matcher = collector.getQueryMatcher();
		// TODO [dg] implement work with collect instead of match 
		
		for( T item : this.items ) {
			if ( matcher.match( item ) ) {
				collector.add( item );
			}
			else {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Item does not match " + item );
				}
			}
		}
	}

	public Class<T> getItemClass() {
		return this.itemClass;
	}

	/**
	 * @param domainObject
	 */
	void add(DomainObject item) {
		if ( !this.itemClass.isInstance(item) ) {
			throw new IllegalArgumentException( "Item " + item + " has invalid class " + this.items );
		}
		this.items.add( this.itemClass.cast(item) );
	}

	/**
	 * @param domainObject
	 */
	public void remove(DomainObject domainObject) {
		this.items.remove( domainObject );
	}

	/**
	 * @param id
	 * @return
	 */
	boolean containsObjectWithId(Long id) {
		return getObjectWithId(id) != null;
	}

	/**
	 * @param id
	 * @return
	 */
	DomainObject getObjectWithId(Long id) {
		for( DomainObject obj : this.items ) {
			if ( obj.getId().equals( id ) ) {
				return obj;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "entity type", this.itemClass.getSimpleName() );
		tsb.append( "size", this.items.size() );
		return tsb.toString();
	}

	
}
