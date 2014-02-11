package ss.lab.dm3.persist;

import ss.lab.dm3.persist.changeset.CrudSet;

/**
 * 
 * @author Dmitry Goncharov
 */
public interface RepositoryListener {

	/**
	 * @param changeSet
	 */
	void repositoryChanged(CrudSet crudSet);

}
