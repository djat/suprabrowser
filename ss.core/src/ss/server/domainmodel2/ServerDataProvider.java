/**
 * 
 */
package ss.server.domainmodel2;

import java.util.List;

import ss.framework.domainmodel2.Criteria;
import ss.framework.domainmodel2.DataProviderException;
import ss.framework.domainmodel2.DataProviderListener;
import ss.framework.domainmodel2.IDataProvider;
import ss.framework.domainmodel2.Record;
import ss.framework.domainmodel2.RunntimeDomainObject;
import ss.framework.domainmodel2.UpdateData;
import ss.framework.domainmodel2.network.UpdateResult;

/**
 *
 */
class ServerDataProvider implements IDataProvider {
	
	private final IDataProvider dbDataProvider;
	
	private final RuntimeDataProvider runtimeDataProvider;

	
	/**
	 * @param dbDataProvider
	 * @param runtimeDataProvider
	 */
	public ServerDataProvider(final IDataProvider dbDataProvider, final RuntimeDataProvider runtimeDataProvider) {
		super();
		this.dbDataProvider = dbDataProvider;
		this.runtimeDataProvider = runtimeDataProvider;
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#addDataProviderListener(ss.framework.domainmodel2.DataProviderListener)
	 */
	public void addDataProviderListener(DataProviderListener listener) {
		this.dbDataProvider.addDataProviderListener(listener);
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#dispose()
	 */
	public void dispose() {
		this.dbDataProvider.dispose();		
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#isAlive()
	 */
	public boolean isAlive() {
		return this.dbDataProvider.isAlive();
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#removeDataProviderListener(ss.framework.domainmodel2.DataProviderListener)
	 */
	public void removeDataProviderListener(DataProviderListener listener) {
		this.dbDataProvider.removeDataProviderListener(listener);
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#selectItems(ss.framework.domainmodel2.Criteria)
	 */
	public List<Record> selectItems(Criteria criteria) throws DataProviderException {
		Class domainObjectClass = criteria.getDomainObjectClass();
		if ( RunntimeDomainObject.class.isAssignableFrom(domainObjectClass)) {
			return this.runtimeDataProvider.selectItems( criteria );			
		} 
		else {
			return this.dbDataProvider.selectItems(criteria);
		}
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#update(ss.framework.domainmodel2.UpdateData)
	 */
	public UpdateResult update(UpdateData data) throws DataProviderException {
		return this.dbDataProvider.update(data);
	}

	
}
