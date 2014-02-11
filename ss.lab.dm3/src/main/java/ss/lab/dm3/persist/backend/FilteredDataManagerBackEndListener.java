/**
 * 
 */
package ss.lab.dm3.persist.backend;

import ss.lab.dm3.events.backend.AbstractNotificatorFilter;
import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.entity.EntityList;
import ss.lab.dm3.persist.changeset.DataChangeSet;
import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.Permission;
import ss.lab.dm3.security2.backend.ISecurityManagerBackEnd;

/**
 * @author Dmitry Goncharov
 */
public class FilteredDataManagerBackEndListener extends AbstractNotificatorFilter<DataManagerBackEndListener> implements DataManagerBackEndListener {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.backend.DataManagerBackEndListener#dataCommitted(ss.lab.dm3.persist.changeset.DataChangeSet)
	 */
	public void dataCommitted(DataChangeSet dataChanges) {
		String contextId = getContext().getId();
		String changeSetContextId = dataChanges.getId().getContextId();
		if ( changeSetContextId == null || !changeSetContextId.equals(contextId) ) {
			if (this.log.isDebugEnabled()) {
				this.log.debug( "Dispatch change set to " + contextId + " from " + changeSetContextId );
			}
			getImpl().dataCommitted(dataChanges);
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug( "Skip dataCommited call for " + contextId + " from " + changeSetContextId );
			}
		}
	}

	// TODO implement dataCommited with security
	public void dataCommittedWithSecurityDraft(DataChangeSet dataChanges) {
		DataChangeSet filteredDataChangeSet = new DataChangeSet( dataChanges.getId(),
			filter( dataChanges.getCreated() ),
			filter( dataChanges.getUpdated() ),
			filter( dataChanges.getDeleted() ) );
		getImpl().dataCommitted( filteredDataChangeSet );		
	}

	/**
	 * @param entities
	 */
	private EntityList filter(EntityList entities) {
		Authentication auth = getContext().getAuthentication();
		final ISecurityManagerBackEnd securityManager = getContext().getSecurityManagerBackEnd();
		EntityList filtered = new EntityList();
		for(Entity entity : entities ) {
			if ( securityManager.hasAccess(auth, entity, Permission.READ ) ) {
				filtered.add(entity);
			}
		}
		return filtered;
	}
}
