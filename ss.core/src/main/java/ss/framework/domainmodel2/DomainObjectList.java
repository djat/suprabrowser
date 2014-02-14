/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
public final class DomainObjectList<D extends DomainObject> {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DomainObjectList.class);
		
	private final Hashtable<QualifiedObjectId, D> idToObject = new Hashtable<QualifiedObjectId, D>();
	
	private final ArrayList<D> orderedObjects = new ArrayList<D>(); 
		
	private final Class<D> baseDomainObjectClass;
	
	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	/**
	 * @param baseDomainObjectClass
	 */
	public DomainObjectList(final Class<D> baseDomainObjectClass) {
		super();
		this.baseDomainObjectClass = baseDomainObjectClass;
	}

//	/**
//	 * @param items
//	 */
//	private DomainObjectList(DomainObjectList<D> items) {
//		this( items.baseDomainObjectClass );
//		SafeIterable<D> iterable = items.lockIterable();
//		try {
//			for( D item : iterable ) {
//				add( item );
//			}
//		}
//		finally {
//			iterable.release();
//		}
//	}

	public void add( D object ) {
		this.readWriteLock.writeLock().lock();
		try {
			if (object == null) {
				throw new ArgumentNullPointerException("object");
			}
			if (!this.baseDomainObjectClass.isInstance(object)) {
				throw new InvalidDomainObjectClassException(
						this.baseDomainObjectClass, object.getClass());
			}
			if (!contains(object)) {
				this.idToObject.put(new QualifiedObjectId(object.getClass(),
						object.getId()), object);
				this.orderedObjects.add(object);
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		} 
	}
	
	public void remove( D object ) {
		this.readWriteLock.writeLock().lock();
		try {
			if (this.idToObject.contains(object.getId())) {
				this.idToObject.remove(object.getId());
				this.orderedObjects.remove(object);
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}

	/**
	 * @return
	 */
	public D getFirst() {
		return size() > 0 ? get( 0 ) : null;	
	}

	/**
	 * @param i
	 * @return
	 */
	private D get(int index) {
		this.readWriteLock.readLock().lock();
		try {
			return this.orderedObjects.get(index);
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}

	/**
	 * @return
	 */
	public int size() {
		this.readWriteLock.readLock().lock();
		try {
			return this.orderedObjects.size();
		} finally {
			this.readWriteLock.readLock().unlock();
		}	
	}

	/**
	 * @param object
	 */
	public boolean contains(D object) {
		this.readWriteLock.readLock().lock();
		try {
			if (object == null) {
				return false;
			}
			D existedObject = getObjectById(object.getClass(), object.getId());
			if (existedObject != null && existedObject != object) {
				logger
						.error("Existed object not equals to added but both have same id. Existed object: "
								+ existedObject + " added " + object);
			}
			return existedObject != null;
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}

	/**
	 * 
	 */
	public void clear() {
		this.readWriteLock.writeLock().lock();
		try {
			this.idToObject.clear();
			this.orderedObjects.clear();
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}

	/**
	 * @return the baseDomainObjectClass
	 */
	public final Class<D> getBaseDomainObjectClass() {
		return this.baseDomainObjectClass;
	}
	
	public final D getObjectById(Class<? extends DomainObject> domainObjectClass, long id) {
		this.readWriteLock.readLock().lock();
		try {
			QualifiedObjectId qualifiedId = new QualifiedObjectId(
					domainObjectClass, id);
			return this.idToObject.get(qualifiedId);
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}
	
	public final D getObjectById(QualifiedObjectId qualifiedId) {
		this.readWriteLock.readLock().lock();
		try {
			return this.idToObject.get(qualifiedId);		
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}	

	/**
	 * @param id
	 * @return
	 */
	public final boolean contains(QualifiedObjectId id) {
		this.readWriteLock.readLock().lock();
		try {
			return this.idToObject.containsKey( id );		
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}

	/**
	 * @param id
	 */
	public D remove(QualifiedObjectId id) {
		this.readWriteLock.writeLock().lock();
		try {
			D object = getObjectById(id);
			if (object != null) {
				remove(object);
			}
			return object;
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}
	
	public LockedIterable<D> lockIterable() {
		return new LockedIterable<D>( this.orderedObjects, this.readWriteLock.readLock() );
	}
	
	
	
}
