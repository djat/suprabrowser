package ss.lab.dm3.persist;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.entity.EntityList;
import ss.lab.dm3.persist.ObjectController.State;

/**
 * @author Dmitry Goncharov
 */
public class DomainObjectSet implements Iterable<DomainObject> {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());
	
	private final Set<DomainObject> objects = new HashSet<DomainObject>();  
	
	private Hashtable<Class<?>, DomainObjectIndex<?>> entityClassToIndex = new Hashtable<Class<?>, DomainObjectIndex<?>>();

	private boolean readonly = false;
	
	public DomainObjectSet() {
	}

	public void add( EntityList entityList, IEntityConvertor convertor, State initialState) {
		for( Entity entity : entityList ) {
			final DomainObject object = convertor.convert(entity, initialState );
			add( object );
		}
	}
	
	public void add(DomainObject domainObject) {
		if ( this.readonly ) {
			throw new IllegalStateException( "Can't modify readonly object set " + this);
		}
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Adding " + domainObject );
		}
		final Class<? extends DomainObject> domainObjectClazz = domainObject.getEntityClass();
		if ( contains( domainObjectClazz, domainObject.getId() ) ) {
//			if ( replace ) {
//				DomainObject existedObject = resolve( domainObjectClazz, domainObject.getId() );
//				existedObject.from( domainObject );
//				return existedObject;
//			}
//			else {
			throw new IllegalArgumentException( "Obect with id " + domainObject.getQualifiedId() + " already exists" );
//			}
		}
		else {
			addToIndexes(domainObject, domainObjectClazz);
			this.objects.add(domainObject);
			// return domainObject;
		}
	}

	/**
	 * @param domainObject
	 * @param domainObjectClazz
	 */
	@SuppressWarnings("unchecked")
	private void addToIndexes(DomainObject domainObject, Class<? extends DomainObject> domainObjectClazz) {
		DomainObjectIndex<? extends DomainObject> index = getOrCreateIndex( domainObjectClazz );
		index.add( domainObject );
		if ( domainObjectClazz != DomainObject.class ) {
			Class<?> superClass = domainObjectClazz.getSuperclass();
			if ( superClass != DomainObject.class ) {
				addToIndexes(domainObject, (Class<? extends DomainObject>)superClass );
			}
		}
	}
	
	/**
	 * @param class1
	 */
	private <T extends DomainObject> DomainObjectIndex<T> getOrCreateIndex(Class<T> entityClazz) {
		DomainObjectIndex<T> index = getIndex(entityClazz);
		if ( index == null ) {
			index = new DomainObjectIndex<T>(entityClazz);
			this.entityClassToIndex.put( index.getItemClass(), index );
		}
		return index;
	}

	@SuppressWarnings("unchecked") 
	<T extends DomainObject> DomainObjectIndex<T> getIndex( Class<T> entityClass ) {
		return (DomainObjectIndex<T>) this.entityClassToIndex.get( entityClass );
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.IDomainObjectSet#collect(ss.lab.dm3.persist.DomainObjectCollector)
	 */
	public <T extends DomainObject> void collect(DomainObjectCollector<T> collector) {
		Class<T> entityClass = collector.getItemClass();
		DomainObjectIndex<T> index = getIndex( entityClass );
		if ( index != null ) {
			index.collect( collector ); 
		}	
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Index not found for " + collector );
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<DomainObject> iterator() {
		return this.objects.iterator();
	}

	/**
	 * @param domainObject
	 */
	public boolean remove(DomainObject domainObject) {
		if ( this.readonly ) {
			throw new IllegalStateException( "Can't modify readonly object set " + this);
		}
		removeFromIndexes(domainObject);
		return this.objects.remove( domainObject );
	}

	/**
	 * @param domainObject
	 */
	private void removeFromIndexes(DomainObject domainObject) {
		removeFromIndexes(domainObject, domainObject.getEntityClass());
	}

	/**
	 * @param domainObject
	 * @param domainObjectClazz
	 */
	@SuppressWarnings("unchecked")
	private void removeFromIndexes(DomainObject domainObject,
			final Class<? extends DomainObject> domainObjectClazz) {
		DomainObjectIndex<? extends DomainObject> index = getIndex(domainObjectClazz);
		if ( index != null ) {
			index.remove( domainObject );
		}
		if ( domainObjectClazz != DomainObject.class ) {
			Class<?> superClass = domainObjectClazz.getSuperclass();
			if ( superClass != DomainObject.class ) {
				removeFromIndexes(domainObject, (Class<? extends DomainObject>)superClass );
			}
		}
	}

	/**
	 * @param domainObject
	 */
	public void ensureObjectRegistered(DomainObject domainObject) {
		DomainObject foundObject = resolveOrNull( domainObject.getEntityClass(), domainObject.getId() );		
		ensureAreSame(domainObject, foundObject, this );
	}

	/**
	 * @param domainObject
	 * @param foundObject
	 */
	static void ensureAreSame(DomainObject domainObject,
			DomainObject foundObject, Object context ) {
		if ( foundObject == null ) {
			 throw new ObjectNotFoundException( "Can't find registered clean object " + domainObject + " in " + context );
		} 
		if ( foundObject != domainObject ) {
			 throw new ObjectNotFoundException( "Find another clean object instance with same id. Clean " + foundObject + ". Given " + domainObject );
		}
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.IDomainObjectSet#clear()
	 */
	public void clear() {
		this.entityClassToIndex.clear();
		this.objects.clear();
	}
	
	public EntityList toDataTransferObjectList() {
		EntityList dtoList = new EntityList();
		for (DomainObject domainObject : this ) {
			Entity tdo = domainObject.toEntity();
			dtoList.add(tdo);
		}
		return dtoList;
	}

	/**
	 * @param changeSetHandler
	 */
	public void copyAndCleanTo(IChangeSetHandler changeSetHandler) {
		for( DomainObject obj : this ) {
			changeSetHandler.addClean( obj );
		}
	}

	/**
	 * @param objectClazz
	 * @param id
	 * @return
	 */
	public boolean contains(Class<? extends DomainObject> objectClazz, Long id) {
		if (objectClazz == null) {
			throw new NullPointerException("objectClazz");
		}
		if (id == null) {
			throw new NullPointerException("id");
		}
		DomainObjectIndex<? extends DomainObject> index = this.getIndex(objectClazz);
		return index != null && index.containsObjectWithId( id );
	}

	/**
	 * @param objectClazz
	 * @param id
	 * @return
	 */
	public <T extends DomainObject> T resolveOrNull(Class<T> objectClazz, Long id) {
		DomainObjectIndex<? extends DomainObject> index = this.getIndex(objectClazz);
		return index != null ? objectClazz.cast(index.getObjectWithId( id )) : null;
	}
		
	public void setState( State state ) {
		for( DomainObject domainObject : this ) {
			domainObject.ctrl.changeState( state );
		}
	}

	/**
	 * @param objectClazz
	 * @return
	 */
	public boolean contains(Class<? extends DomainObject> objectClazz) {
		return this.getIndex(objectClazz) != null;
	}

	/**
	 * @param qualifiedId
	 * @return
	 */
	public boolean contains(QualifiedObjectId<? extends DomainObject> qualifiedId) {
		return contains( qualifiedId.getObjectClazz(), qualifiedId.getId() );
	}
	
	/**
	 * @return
	 */
	public int size() {
		return this.objects.size();
	}

	/**
	 * 
	 */
	public void detachAll() {
		for( DomainObject object : this ) {
			object.ctrl.changeState( State.DETACHED );
		}
	}

	/**
	 * 
	 */
	public Object[] toArray() {
		return this.objects.toArray();
	}

	/**
	 * @return
	 */
	public String toDebugString() {
		ToStringBuilder tsb = new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE ); 
		tsb.append( "objects", this.objects );
		return tsb.toString();
	}

	/**
	 * @param deleted
	 */
	public void add(Iterable<DomainObject> objects ) {
		for( DomainObject obj : objects ) {
			add( obj );
		}
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return this.objects.isEmpty();
	}
	
	Set<DomainObject> getObjects() {
		return this.objects;
	}

	public boolean isReadonly() {
		return this.readonly;
	}

	public void makeReadonly() {
		this.readonly = true;
	}

	/**
	 * @param objectId
	 * @return
	 */
	public <T extends DomainObject> T resolveOrNull(QualifiedObjectId<T> objectId) {
		return objectId != null ? resolveOrNull(objectId.getObjectClazz(), objectId.getId()) : null;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("size", this.size() );
		return tsb.toString();
	}

	
	
	
}
