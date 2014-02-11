package ss.lab.dm3.persist.changeset;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.entity.EntityList;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.DomainObjectSet;
import ss.lab.dm3.persist.IChangeSetHandler;
import ss.lab.dm3.persist.IEntityConvertor;
import ss.lab.dm3.persist.ObjectRegisteredException;
import ss.lab.dm3.persist.ObjectController.State;
import ss.lab.dm3.persist.space.Space;

/**
 * @author Dmitry Goncharov 
 */
public class ChangeSet {
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final ChangeSetId id;
	
	private final Space space;
	
	protected final List<DomainObject> loadedObjects = new ArrayList<DomainObject>();
	
	protected final DomainObjectSet newObjects;
	
	protected final DomainObjectSet dirtyObjects;

	protected final DomainObjectSet removedObjects;
	
	protected boolean loadResult = false;
	
	/**
	 * @param domain 
	 * 
	 */
	public ChangeSet(Domain domain) {
		this( null, null );
	}

	public ChangeSet(ChangeSetId id ) {
		this( id, null );
	}
	
	public ChangeSet(ChangeSetId id, Space space ) {
		this(id, space, new DomainObjectSet(), new DomainObjectSet(), new DomainObjectSet() );
	}
		/**
	 * @param id
	 * @param newObjects
	 * @param dirtyObjects
	 * @param removedObjects
	 */
	private ChangeSet(ChangeSetId id, Space space, DomainObjectSet newObjects,
			DomainObjectSet dirtyObjects, DomainObjectSet removedObjects) {
		super();
		this.id = id;
		this.space = space;
		this.newObjects = newObjects;
		this.dirtyObjects = dirtyObjects;
		this.removedObjects = removedObjects;
	}

	public ChangeSet(DataChangeSet dataChangeSet, IEntityConvertor convertor, Space space) {
		this( dataChangeSet.getId(), space );
		this.newObjects.add(dataChangeSet.getCreated(), convertor, State.NEW );
		this.dirtyObjects.add(dataChangeSet.getUpdated(), convertor, State.DIRTY );
		this.removedObjects.add(dataChangeSet.getDeleted(), convertor, State.REMOVED );
	}

	public ChangeSet(EntityList loadedObjects, IEntityConvertor convertor, Space space ) {
		this( null, space );
		this.loadResult = true;
		for( Entity entity : loadedObjects ) {
			final DomainObject object = convertor.convert(entity, State.DIRTY );
			this.dirtyObjects.add( object );
			this.loadedObjects.add( object );
		}		
	}

	public Space getSpace() {
		return this.space;
	}

	public void apply(IChangeSetHandler changeSetHandler) {
		this.newObjects.copyAndCleanTo(changeSetHandler);
		this.dirtyObjects.copyAndCleanTo(changeSetHandler);
		this.removedObjects.detachAll();
	}
			
	public void remove(DomainObject domainObject) {
		this.dirtyObjects.remove(domainObject);
		this.newObjects.remove(domainObject);
		this.removedObjects.remove(domainObject);
	}
	
	public Iterable<DomainObject> getNewObjects() {
		return this.newObjects;
	}

	public Iterable<DomainObject> getDirtyObjects() {
		return this.dirtyObjects;
	}

	public Iterable<DomainObject> getRemovedObjects() {
		return this.removedObjects;
	}

	public List<DomainObject> getLoadedObjects() {
		return this.loadedObjects;
	}

	/**
	 * @param dirty
	 * @return
	 */
	protected boolean isRegisteredWithSameId(DomainObject domainObject) {
		Class<? extends DomainObject> objectClazz = domainObject.getEntityClass();
		Long id = domainObject.getId();
		return this.dirtyObjects.contains(objectClazz, id)
				|| this.newObjects.contains(objectClazz, id)
				|| this.removedObjects.contains(objectClazz, id);
	}

	/**
	 * @param domainObject
	 */
	protected void checkNotRegisteredWithSameId(DomainObject domainObject) {
		if ( isRegisteredWithSameId(domainObject) ) {
			throw new ObjectRegisteredException( domainObject );
		}
		
	}
	
	/**
	 * @return the id
	 */
	public ChangeSetId getId() {
		return this.id;
	}

	/**
	 * @return
	 */
	public DataChangeSet toDataChangeSet() {
		EntityList created = this.newObjects
				.toDataTransferObjectList();
		EntityList updated = this.dirtyObjects
				.toDataTransferObjectList();
		EntityList deleted = this.removedObjects
				.toDataTransferObjectList();
		return new DataChangeSet( this.id, created, updated, deleted);
	}

	/**
	 * @return
	 */
	public String toDebugString() {
		ToStringBuilder tsb = new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE );
		tsb.append( "id", this.id );
		tsb.append( "newObjects", this.newObjects.toDebugString() );
		tsb.append( "dirtyObjects", this.dirtyObjects.toDebugString() );
		tsb.append( "removedObjects", this.removedObjects.toDebugString() );
		tsb.append( "loadedObjects", this.loadedObjects );
		return tsb.toString();
	}

	public boolean isLoadResult() {
		return this.loadResult;
	}
	
}
