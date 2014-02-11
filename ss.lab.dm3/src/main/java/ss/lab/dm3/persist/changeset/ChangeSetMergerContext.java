package ss.lab.dm3.persist.changeset;

import java.util.HashSet;
import java.util.Set;

import ss.lab.dm3.persist.DomainObject;

/**
 * @author Dmitry Goncharov 
 */
public class ChangeSetMergerContext {

	private final ChangeSet incomingChangeSet;
	
	private final Set<DomainObject> intersectedObjects = new HashSet<DomainObject>();
	/**
	 * @param incomingChangeSet
	 */
	public ChangeSetMergerContext(ChangeSet incomingChangeSet) {
		this.incomingChangeSet = incomingChangeSet;
	}

	public void registryIncommingIntersection( DomainObject domainObject ) {
		this.intersectedObjects.add( domainObject );
	}

	/**
	 * Incoming
	 */
	public ChangeSet getIncomingChangeSet() {
		return this.incomingChangeSet;
	}
	/**
	 * 
	 */
	public void filterIncomingChangeSet() {
		for( DomainObject intersectedObject : this.intersectedObjects ) {
			this.incomingChangeSet.remove(intersectedObject);
		}
	}
	
}
