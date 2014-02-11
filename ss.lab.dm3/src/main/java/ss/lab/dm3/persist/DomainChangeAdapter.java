package ss.lab.dm3.persist;

import ss.lab.dm3.persist.changeset.CrudSet;

public class DomainChangeAdapter implements DomainChangeListener {

	private final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	/* (non-Javadoc)
	 * @see sim.client.ui2.DomainChangeListener#domainChanged(ss.lab.dm3.persist.changeset.CrudSet)
	 */
	public void domainChanged(CrudSet changeSet) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Domain changed " + changeSet );
		}
	}


}
