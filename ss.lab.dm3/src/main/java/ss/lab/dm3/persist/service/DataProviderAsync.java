package ss.lab.dm3.persist.service;

import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.connection.service.ServiceAsync;
import ss.lab.dm3.persist.Query;
import ss.lab.dm3.persist.changeset.DataChangeSet;

/**
 * @author Dmitry Goncharov
 */
public interface DataProviderAsync extends ServiceAsync {

	/**
	 * Returns DataTransferObjectList
	 * @param criteria
	 */
	void selectData( Query criteria, ICallbackHandler handler );

	/**
	 * @param dataChanges
	 * @param commitHandler
	 */
	void commitData( DataChangeSet dataChanges, ICallbackHandler commitHandler);

	/**
	 * Returns MapperManagerParamerts<DomainObject>
	 */
	void getMapperManagerParamerts( ICallbackHandler handler );
	
}
