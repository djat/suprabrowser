/**
 * 
 */
package ss.server.domainmodel2.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 */
public interface IResultSetRowHandler {

	/**
	 * @param rs result set row
	 * @throws SQLException 
	 */
	void handleRow(ResultSet rs) throws ResultSetRowHandlerException, SQLException;
	

}
