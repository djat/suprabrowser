package ss.lab.dm3.persist.changeset;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.DomainObjectSet;
import ss.lab.dm3.persist.IChangeSetHandler;

public class CleanObjectsOverwriter {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final ChangeSet externalObjects;
	
	private final DomainObjectSet createdObjects;
	private final DomainObjectSet updatedObjects = new DomainObjectSet();
	private final DomainObjectSet retrievedObjects = new DomainObjectSet();
	private final DomainObjectSet deletedObjects = new DomainObjectSet();
	private List<DomainObject> loadedObjects;


	/**
	 * @param externalObjects
	 * @param mapper
	 */
	public CleanObjectsOverwriter(ChangeSet externalObjects) {
		super();
		this.externalObjects = externalObjects;
		this.createdObjects = externalObjects.newObjects;
	}

	/**
	 * @param cleanObjects
	 */
	public void overwrite(IChangeSetHandler changeSetHandler) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Overwrite " + changeSetHandler );
		}		
		// Add all new objects
		this.createdObjects.copyAndCleanTo( changeSetHandler );		
		// Add retrieved and update changed objects 
		for (DomainObject externalDirty : this.externalObjects.dirtyObjects) {
			DomainObject cleanObject = changeSetHandler.addCleanOrUpdate(externalDirty);
			if (this.log.isDebugEnabled()) {
				this.log.debug( "clean or update " + cleanObject );
			}
			if ( cleanObject != null ) {
				if ( cleanObject == externalDirty ) {
					this.retrievedObjects.add( cleanObject );				
				}
				else {
					this.updatedObjects.add( cleanObject );
				}
			}
		}
		// Delete removed objects 
		for (DomainObject externalRemoved : this.externalObjects.removedObjects) {
			DomainObject cleanObject = changeSetHandler.resolveOrNull(
					externalRemoved.getEntityClass(), externalRemoved.getId());
			if (cleanObject != null) {
				changeSetHandler.removeAndDetach(cleanObject);
				this.deletedObjects.add(cleanObject);
			}
		}
		if ( this.externalObjects.isLoadResult() ) {
			this.loadedObjects = new ArrayList<DomainObject>();
			for( DomainObject external : this.externalObjects.getLoadedObjects() ) {
				final DomainObject resolved = changeSetHandler.resolveOrNull( external.getEntityClass(), external.getId() );
				//TODG problem with locally deleted objects. If resolved is null?
				this.loadedObjects.add( resolved );
			}
		}
	}

	public CrudSet getCrudSet() {
		return new CrudSet( this.createdObjects,
				this.retrievedObjects,
				this.updatedObjects, 
				this.deletedObjects, 
				this.loadedObjects );
	}
	
}
