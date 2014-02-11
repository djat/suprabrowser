/**
 * 
 */
package ss.lab.dm3.persist.backend;

import ss.lab.dm3.events.EventListener;
import ss.lab.dm3.persist.changeset.DataChangeSet;

/**
 * 
 */
public interface DataManagerBackEndListener extends EventListener {

	void dataCommitted(DataChangeSet dataChanges);

}