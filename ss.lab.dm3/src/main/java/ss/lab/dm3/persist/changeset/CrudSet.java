package ss.lab.dm3.persist.changeset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.DomainObjectSet;
import ss.lab.dm3.persist.IObjectMatcher;
import ss.lab.dm3.persist.space.Space;

public class CrudSet {
	
	private static final List<DomainObject> EMPTY = new ArrayList<DomainObject>();

	private ChangeSet originalChangeSet;

	private DomainObjectSet alive = null;
	
	private final List<DomainObject> loaded; 
	
	private final DomainObjectSet created;

	private final DomainObjectSet retrieved;

	private final DomainObjectSet updated;

	private final DomainObjectSet deleted;
	
	private boolean frozen = false;
	
	public CrudSet(DomainObjectSet created, DomainObjectSet readed, DomainObjectSet updated,
			DomainObjectSet deleted ) {
		this( created, readed, updated, deleted, null );
	}
	/**
	 * @param created
	 * @param readed
	 * @param updated
	 * @param deleted
	 */
	public CrudSet(DomainObjectSet created, DomainObjectSet readed, DomainObjectSet updated,
			DomainObjectSet deleted, List<DomainObject> loaded) {
		super();
		this.created = created;
		this.retrieved = readed;
		this.updated = updated;
		this.deleted = deleted;		
		this.loaded = loaded;
	}

	/**
	 * 
	 */
	public void froze() {
		if ( !this.frozen )  {
			this.frozen  = true;
			this.created.makeReadonly();
			this.retrieved.makeReadonly();
			this.updated.makeReadonly();
			this.deleted.makeReadonly();
			this.alive = new DomainObjectSet();			
			this.alive.add( this.created );
			this.alive.add( this.retrieved );
			this.alive.add( this.updated );
		}
	}

	/**
	 * @param changeSet
	 */
	public CrudSet(TransactionChangeSet changeSet) {
		this(changeSet.newObjects, new DomainObjectSet(), changeSet.dirtyObjects,
				changeSet.removedObjects);
	}
	
	/**
	 * @return
	 */
	public Space getSpace() {
		return this.originalChangeSet != null ? this.originalChangeSet.getSpace() : null;
	}
	
	public ChangeSet getOriginalChangeSet() {
		return this.originalChangeSet;
	}

	public void setOriginalChangeSet(ChangeSet originalChangeSet) {
		this.originalChangeSet = originalChangeSet;
	}	

	public DomainObjectSet getCreated() {
		return this.created;
	}

	public DomainObjectSet getRetrieved() {
		return this.retrieved;
	}

	public DomainObjectSet getUpdated() {
		return this.updated;
	}

	public DomainObjectSet getDeleted() {
		return this.deleted;
	}

	/**
	 * @param objectClazz
	 * @return
	 */
	public boolean contains(Class<? extends DomainObject> objectClazz) {
		if ( objectClazz == DomainObject.class ) {
			return !isEmpty();
		}
		else {
			return this.created.contains(objectClazz) || this.retrieved.contains(objectClazz)
				|| this.updated.contains(objectClazz) || this.deleted.contains(objectClazz);
		}
	}

	public <T extends DomainObject> int applyTo(Class<T> entityClazz, Collection<T> items) {
		return applyTo(entityClazz, items, null);
	}

	public <T extends DomainObject> int applyTo(Class<T> entityClazz, Collection<T> items,
			IObjectMatcher entityFilter) {
		int changesCount = 0;
		if ( items == null ) {
			throw new NullPointerException( "items" );
		}
		for (DomainObject createdObject : this.created) {
			if (entityClazz.isInstance(createdObject)) {
				T typedObject = entityClazz.cast(createdObject);
				if (entityFilter == null || entityFilter.match(typedObject)) {
					items.add(typedObject);
					++ changesCount;
				}
			}
		}
		for (DomainObject retrievedObject : this.retrieved) {
			if (entityClazz.isInstance(retrievedObject)) {
				T typedObject = entityClazz.cast(retrievedObject);
				if (entityFilter == null || entityFilter.match(typedObject)) {
					items.add(typedObject);
					++ changesCount;
				}
			}
		}
		for (DomainObject deletedObject : this.deleted) {
			if (entityClazz.isInstance(deletedObject)) {
				T typedObject = entityClazz.cast(deletedObject);
				if (entityFilter == null || entityFilter.match(typedObject)) {
					items.remove(typedObject);
					++ changesCount; 
				}
			}
		}
		return changesCount;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("created", this.created.size());
		tsb.append("retrieved", this.retrieved.size());
		tsb.append("updated", this.updated.size());
		tsb.append("deleted", this.deleted.size());
		return tsb.toString();
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return this.created.isEmpty() 
			&& this.retrieved.isEmpty() 
			&& this.updated.isEmpty()
			&& this.deleted.isEmpty();
	}

	/**
	 * 
	 */
	public DomainObjectSet getAlive() {
		if ( !this.frozen ) {
			throw new IllegalStateException( "Can't get alive for not frozed crud set " + this );
		}		
		return this.alive;
	}
	
	public List<DomainObject> getLoaded() {
		return isLoadResult() ? this.loaded : EMPTY;
	}
	
	public boolean isLoadResult() {
		return this.loaded != null;
	}
	
	/**
	 * @return
	 */
	public boolean containsModifications() {
		return this.created.size() > 0 ||
			this.updated.size() > 0 ||
			this.deleted.size() > 0;
	}
	
	public DomainObject resolve(QualifiedObjectId<DomainObject> id) { 
		DomainObject domainObject = this.created.resolveOrNull(id);
		if ( domainObject != null ) {
			return domainObject;
		}
		domainObject = this.retrieved.resolveOrNull(id);
		if ( domainObject != null ) {
			return domainObject;
		}
		domainObject = this.updated.resolveOrNull(id);
		if ( domainObject != null ) {
			return domainObject;
		}
		return null;
	}
		
}
