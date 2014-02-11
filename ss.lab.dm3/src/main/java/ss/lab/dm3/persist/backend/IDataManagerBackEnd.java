package ss.lab.dm3.persist.backend;

import ss.lab.dm3.blob.backend.BlobInformationProvider;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.MapperParamerts;
import ss.lab.dm3.persist.Query;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.changeset.DataChangeSet;

/**
 * @author Dmitry Goncharov
 */
public interface IDataManagerBackEnd {

	/**
	 * @param criteria
	 * @return
	 */
	EntitiesSelectResult selectData(Query criteria);

	/**
	 * @param dataChanges
	 */
	void commitData(DataChangeSet dataChanges);

	/**
	 * @return the dataMapperManager
	 */
	Mapper<DomainObject> getMapper();

	/**
	 * @return
	 */
	MapperParamerts getMapperParamerts();

	BlobInformationProvider getBlobInformationProvider();
	
	void searchReindex();
}