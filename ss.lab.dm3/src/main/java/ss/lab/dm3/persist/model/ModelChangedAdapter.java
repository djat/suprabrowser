package ss.lab.dm3.persist.model;

import ss.lab.dm3.persist.changeset.CrudSet;

public class ModelChangedAdapter implements ModelChangedListener {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	/* (non-Javadoc)
	 * @see sim.client.ui2.spheremanagement.model.ModelChangedListener#modelChanged(ss.lab.dm3.persist.changeset.CrudSet)
	 */
	public void modelChanged(CrudSet changeSet) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Model changed " + changeSet );
		}
	}

}
