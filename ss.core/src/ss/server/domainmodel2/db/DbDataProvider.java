/**
 * 
 */
package ss.server.domainmodel2.db;

import java.util.List;

import ss.framework.domainmodel2.AbstractDataProvider;
import ss.framework.domainmodel2.ChangedData;
import ss.framework.domainmodel2.Criteria;
import ss.framework.domainmodel2.DataChangedEvent;
import ss.framework.domainmodel2.DataProviderException;
import ss.framework.domainmodel2.Record;
import ss.framework.domainmodel2.UpdateData;
import ss.framework.domainmodel2.network.UpdateResult;

/**
 *
 */
public final class DbDataProvider extends AbstractDataProvider{

	private final DataMapper dataMapper;
	
	public DbDataProvider( String dbUrl ) {
		this.dataMapper = new DataMapper( dbUrl );
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#selectItems(ss.framework.domainmodel2.Criteria)
	 */
	public synchronized List<Record> selectItems(Criteria criteria) throws DataProviderException {
		return this.dataMapper.select(criteria);
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.AbstractDataProvider#disposing()
	 */
	@Override
	protected void disposing() {
		super.disposing();
		this.dataMapper.dispose();
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#sendUpdate(ss.framework.domainmodel2.UpdateData)
	 */
	public UpdateResult update(UpdateData data) throws DataProviderException {
		this.dataMapper.update( data );		
		ChangedData changedData = new ChangedData( data.getOwnerId() );
		changedData.addCreated( data.getNewRecords() );
		changedData.addModified( data.getDirtyRecords() );
		changedData.addRemoved( data.getRemovedRecords() );
		notifyDataChanged( new DataChangedEvent( changedData, true ) );
		return new UpdateResult( changedData );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.IDataProvider#isAlive()
	 */
	public boolean isAlive() {
		return true;
	}

	/**
	 * @return the dataMapper
	 */
	public DataMapper getDataMapper() {
		return this.dataMapper;
	}
	
	
}
