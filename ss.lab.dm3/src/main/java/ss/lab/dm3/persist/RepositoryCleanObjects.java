package ss.lab.dm3.persist;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.ObjectController.State;

public class RepositoryCleanObjects implements IChangeSetHandler {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final DomainObjectSet clean = new DomainObjectSet(); 
	
	public void addClean(DomainObject externalObject) {
		final QualifiedObjectId<? extends DomainObject> objectId = externalObject.getQualifiedId();
		externalObject.ctrl.changeState( State.CLEAN );
		if ( this.clean.contains( objectId ) ) {
			throw new IllegalArgumentException( "Object already registered " + externalObject );	
		}
		if (this.log.isDebugEnabled()) {
			this.log.debug( "addClean " + externalObject );
		}
		this.clean.add(externalObject);
	}
	
	public void unregistryAndDetach( DomainObject object ) {
		unregistry( object, false );
		object.ctrl.changeState( State.DETACHED );
	}
	
	public DomainObject addCleanOrUpdate(DomainObject externalObject) {
		// TODO simplify this code
		boolean skipUpdate = false;
		final QualifiedObjectId<? extends DomainObject> objectId = externalObject.getQualifiedId();
		DomainObject internalObject = this.clean.resolveOrNull(objectId);
		if (internalObject != null) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Found existed object " + internalObject );
			}
			this.clean.remove(internalObject);
			// Update existed object state
			boolean updated = internalObject.ctrl.from(externalObject, true);
			if ( !updated ) {
				internalObject.ctrl.setGeneration( externalObject.ctrl.getGeneration() );
				skipUpdate = true;
			}
			if (this.log.isDebugEnabled()) {
				this.log.debug("Internal object updated " + internalObject );
			}
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Object not found " + objectId );
			}
			internalObject = externalObject;
		}
		addClean(internalObject);
		return skipUpdate ? null : internalObject;
	}

	public <T extends DomainObject> T resolveOrNull(QualifiedObjectId<T> id) {
		return resolveOrNull(id.getObjectClazz(), id.getId() );
	}
	
	public <T extends DomainObject> T resolveOrNull(Class<T> objectClazz, Long id) {
		DomainObject object = this.clean.resolveOrNull( objectClazz, id );		
		if (this.log.isDebugEnabled()) {
			this.log.debug("Resolve " + objectClazz.getSimpleName() + "#" + id + ", result  " + object );
		}		
		return objectClazz.cast( object );
	}

	public void removeAndDetach(DomainObject externalObject) {
		final QualifiedObjectId<? extends DomainObject> objectId = externalObject.getQualifiedId();
		DomainObject internalObject = this.clean.resolveOrNull(objectId);
		if ( internalObject != null ) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Found clean object related to external " + externalObject );
			}
			this.clean.remove(internalObject);
			internalObject.ctrl.changeState( State.DETACHED );			
		}
		ensureDoesNotContain( objectId );
	}
	
	private void ensureDoesNotContain( QualifiedObjectId<? extends DomainObject> objectId) {
		if ( this.clean.contains(objectId) ) {
			throw new IllegalStateException( "Clean object contains object with id " + objectId );
		}
	}
		
	public boolean unregistry(DomainObject domainObject, boolean checkObjectIsRegistered ) {
		boolean removed = this.clean.remove(domainObject);
		// TODO think about replacing exception by log.error|log.warn
		if ( checkObjectIsRegistered && !removed ) {
			throw new ObjectNotFoundException( "Can't find registered clean object " + domainObject + " in " + this );
		}		
		ensureDoesNotContain( domainObject.getQualifiedId() );
		return removed;
	}
	
	public void clear() {
		this.clean.clear();
	}
	
	public <T extends DomainObject> void collect(DomainObjectCollector<T> collector) {
		this.clean.collect(collector);
	}

	public void debugTo(ToStringBuilder tsb) {
		tsb.append( "Clean objects state ", this );
	}
	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "cleanObjects", this.clean.toString() );
		return tsb.toString();
	}

	public int size() {
		return this.clean.size();
	}

	public boolean contains(QualifiedObjectId<? extends DomainObject> qualifiedId) {
		return this.clean.contains(qualifiedId);
	}	

}
