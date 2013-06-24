/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class DomainObjectMap {

	private final DomainObjectList<DomainObject> objects = new DomainObjectList<DomainObject>(DomainObject.class);
	
		/**
	 * @return
	 * @see ss.framework.domainmodel2.DomainObjectList#lockIterable()
	 */
	public LockedIterable<DomainObject> lockIterable() {
		return this.objects.lockIterable();
	}

	/**
	 * @param dirtyObjects
	 */
	public final void add(DomainObject object) {
		this.objects.add(object);
	}

	/**
	 * 
	 */
	public final void clear() {
		this.objects.clear();		
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDomainSpaceObjects#getSingleObject(ss.framework.domainmodel2.Criteria)
	 */
	public final <D extends DomainObject> D getSingleObject(Criteria<D> criteria ) {
		DomainObjectList<D> result = new DomainObjectList<D>( criteria.getDomainObjectClass() );
		collectObjects( result, criteria );
		return result.getFirst();
	}

	/**
	 * @param object
	 */
	public final boolean contains(DomainObject object) {
		return this.objects.contains( object );
	}


	/**
	 * @param object
	 */
	public void remove(DomainObject object) {
		this.objects.remove(object);		
	}
	
	/**
	 * @param id
	 */
	public final DomainObject remove(QualifiedObjectId id) {
		return this.objects.remove(id);
	}
	
	public final <D extends DomainObject> void collectObjects(DomainObjectList<D> result, Criteria<D> criteria) {
		Class<D> resultDomainObjectClass = criteria.getDomainObjectClass();
		LockedIterable<DomainObject> iterable = this.objects.lockIterable();
		try {
			for (DomainObject domainObject : iterable ) {
				if (criteria.match(domainObject)) {
					result.add(resultDomainObjectClass.cast(domainObject));
				}
			}
		} finally {
			iterable.release();
		}
	}

	/**
	 * @param id
	 * @return
	 */
	public final <D extends DomainObject> D getObjectById(Class<D> domainObjectClass, long id) {
		return domainObjectClass.cast( this.objects.getObjectById( domainObjectClass, id) );
	}

	/**
	 * @return
	 */
	public final List<Record> toRecords() {
		List<Record>  records = new ArrayList<Record>(); 
		LockedIterable<DomainObject> iterable = lockIterable();
		try {
			for (DomainObject object : iterable ) {
				Record record = new Record(object.getClass());
				object.save(record);
				records.add(record);
			}
		} finally {
			iterable.release();
		}
		return records;
	}

	/**
	 * @param id
	 */
	public boolean contains(QualifiedObjectId id) {
		return this.objects.contains( id );		
	}

	/**
	 * @param newObjects
	 */
	public void addAll(Iterable<DomainObject> items) {
		for( DomainObject item : items ) {
			add( item );
		}
	}


	
}
