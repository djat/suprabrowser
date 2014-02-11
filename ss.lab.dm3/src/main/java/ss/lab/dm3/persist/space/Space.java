package ss.lab.dm3.persist.space;

import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.changeset.CrudSet;


/**
 * 
 * @author Dmitry Goncharov
 *  
 */
public abstract class Space {
	
	public abstract boolean shouldExpandBy( DomainObject object );

	public void afterChangeSetApplied( SpaceRegistry repository, CrudSet changeSet ) {
	}
	
}