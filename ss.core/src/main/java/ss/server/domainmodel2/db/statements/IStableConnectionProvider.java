/**
 * 
 */
package ss.server.domainmodel2.db.statements;

import java.sql.Connection;
import java.sql.SQLException;


/**
 *
 */
public interface IStableConnectionProvider {

	/**
	 *	
	 */
	Connection getConnection() throws SQLException;

	/**
	 * 
	 */
	void dispose();

}
