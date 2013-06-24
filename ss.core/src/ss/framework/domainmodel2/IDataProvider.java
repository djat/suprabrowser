/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.List;

import ss.framework.domainmodel2.network.UpdateResult;

/**
 *
 */
public interface IDataProvider {

	List<Record> selectItems(Criteria criteria) throws DataProviderException;
	
	UpdateResult update( UpdateData data ) throws DataProviderException;	
	
	void dispose();

	void addDataProviderListener(DataProviderListener listener);
	
	void removeDataProviderListener(DataProviderListener listener);

	boolean isAlive();

};
