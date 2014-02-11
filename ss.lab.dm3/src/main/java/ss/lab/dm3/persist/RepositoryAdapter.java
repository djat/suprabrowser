package ss.lab.dm3.persist;

import ss.lab.dm3.persist.changeset.CrudSet;

public class RepositoryAdapter implements RepositoryListener {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.RepositoryListener#repositoryChanged(ss.lab.dm3.persist.changeset.CrudSet)
	 */
	public void repositoryChanged(CrudSet crudSet) {
		// TODO Auto-generated method stub		
	}
	

}
