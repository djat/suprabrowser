package ss.lab.dm3.persist.changeset;

import ss.lab.dm3.persist.DomainObject;

/**
 * @author Dmitry Goncharov 
 */
public class EditableChangeSet extends ChangeSet {

	/**
	 * @param id
	 */
	public EditableChangeSet(ChangeSetId id) {
		super(id);
	}

	public void addDirty(DomainObject domainObject) {
		checkNotRegisteredWithSameId( domainObject );
		this.dirtyObjects.add(domainObject);
	}
	
	public void addRemoved(DomainObject domainObject ) {
		checkNotRegisteredWithSameId( domainObject );
		this.removedObjects.add(domainObject);
	}
	
	public void addNew(DomainObject domainObject) {
		checkNotRegisteredWithSameId( domainObject );
		this.newObjects.add(domainObject);
	}
}
