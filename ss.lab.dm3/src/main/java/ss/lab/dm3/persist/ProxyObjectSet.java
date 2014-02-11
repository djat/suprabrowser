package ss.lab.dm3.persist;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.WeakHashMap;

import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * TODO Fix problem with secondary setUpManagedFratures call
 * TODO Check that object unloading don't waste space 
 *  
 * @author Dmitry Goncharov
 */
public class ProxyObjectSet implements Iterable<QualifiedObjectId<? extends DomainObject>> {

	private WeakHashMap<QualifiedObjectId<? extends DomainObject>, WeakReference<DomainObject>> idToObject = new WeakHashMap<QualifiedObjectId<? extends DomainObject>, WeakReference<DomainObject>>();
	
	public void add(DomainObject object) {
		if ( object == null ) {
			throw new NullPointerException( "object");
		}
//		if ( !object.ctrl.isProxy() ) {
//			throw new IllegalArgumentException( "object is not proxy " + object );
//		}
		final QualifiedObjectId<? extends DomainObject> qualifiedId = object.getQualifiedId();
		if ( contains( qualifiedId ) ) {
			throw new IllegalArgumentException( "Can't add object " + object + ", proxy with same id already exists." );
		}
		this.idToObject.put( qualifiedId, new WeakReference<DomainObject>(object) );
	}
	
	/**
	 * @param qualifiedId
	 * @return
	 */
	private boolean contains(QualifiedObjectId<? extends DomainObject> qualifiedId) {
		return resolve(qualifiedId) != null;
	}

	public <T extends DomainObject> T resolve( Class<T> objectClazz, Long id ) {
		return resolve( new QualifiedObjectId<T>( objectClazz, id ) );
	}

	/**
	 * @param objectId
	 * @return
	 */
	public <T extends DomainObject> T resolve(QualifiedObjectId<T> objectId) {
		final WeakReference<DomainObject> ref = this.idToObject.get( objectId );
		if ( ref != null ) {
			return objectId.getObjectClazz().cast( ref.get() );
		}
		else {
			return null;
		}
	}
	
	public void remove(DomainObject object ) {
		this.idToObject.remove( object.getQualifiedId() );
	}

	public void clear() {
		this.idToObject.clear();
	}

	/**
	 * @return
	 */
	public int size() {
		return this.idToObject.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<QualifiedObjectId<? extends DomainObject>> iterator() {
		return this.idToObject.keySet().iterator();
	}
	
	
}
