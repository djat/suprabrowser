package ss.lab.dm3.persist.service;

import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.connection.service.Service;
import ss.lab.dm3.orm.mapper.MapperParamerts;
import ss.lab.dm3.persist.Query;
import ss.lab.dm3.persist.backend.EntitiesSelectResult;
import ss.lab.dm3.persist.changeset.DataChangeSet;

/**
 * @author Dmitry Goncharov
 */
public interface DataProvider extends Service {

	EntitiesSelectResult selectData(Query criteria) throws ServiceException;
	
	void commitData( DataChangeSet dataChanges) throws ServiceException;
	
	MapperParamerts getMapperManagerParamerts() throws ServiceException;
		
}
