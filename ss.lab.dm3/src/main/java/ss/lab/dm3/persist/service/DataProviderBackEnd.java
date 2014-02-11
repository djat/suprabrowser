package ss.lab.dm3.persist.service;

import ss.lab.dm3.connection.service.ServiceBackEnd;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.orm.mapper.MapperParamerts;
import ss.lab.dm3.persist.Query;
import ss.lab.dm3.persist.backend.IDataManagerBackEnd;
import ss.lab.dm3.persist.backend.EntitiesSelectResult;
import ss.lab.dm3.persist.changeset.ChangeSetId;
import ss.lab.dm3.persist.changeset.DataChangeSet;
/**
 * @author Dmitry Goncharov
 */
public class DataProviderBackEnd extends ServiceBackEnd implements DataProvider {
	
	IDataManagerBackEnd backEnd;
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.connection.service.ServiceBackEnd#initializing()
	 */
	@Override
	protected void initializing() {
		super.initializing();
		this.backEnd = getContext().getDataManagerBackEnd();
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persistentobjects.services.DataProviderService#selectData(ss.lab.dm3.persistentobjects.Criteria)
	 */
	public EntitiesSelectResult selectData(Query criteria) throws ServiceException {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Selecting data by " + criteria );
		}
		return this.backEnd.selectData( criteria );		
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.services.DataProviderFrontEnd#commitData(ss.lab.dm3.persist.DataChangesSet)
	 */
	public void commitData(DataChangeSet dataChanges)
			throws ServiceException {
		final ChangeSetId contextedId = new ChangeSetId( getContext().getId(), dataChanges.getId().getChangeId() );
		DataChangeSet dataChangesWithContextedId = new DataChangeSet( contextedId, dataChanges.getCreated(), dataChanges.getUpdated(), dataChanges.getDeleted() );
		this.backEnd.commitData( dataChangesWithContextedId );
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.services.DataProvider#getDomainMaps()
	 */
	public MapperParamerts getMapperManagerParamerts()
			throws ServiceException {
		checkInitialed();
		return this.backEnd.getMapperParamerts();
	}

}
