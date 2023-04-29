/**
 * 
 */
package ss.server.domainmodel2.db.statements;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.PooledConnection;

import com.mysql.cj.jdbc.MysqlPooledConnection;

import ss.server.db.DBPool.InstantiationConnectionException;


/**
 * 
 */
public final class StableConnectionProvider implements IStableConnectionProvider {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(StableConnectionProvider.class);

	private static int POOLED_CONNECTION_COUNTER;
	
	// Will be removed, when we will be 
	// private static final String TEST_QUERY = "select connection_id()";

	private PooledConnection pooledConnection = null;

	private IConnectionFactory connectionFactory;

	/**
	 * @param dbUrl
	 */
	public StableConnectionProvider(String dbUrl) {
		this( new ConnectionFactory( dbUrl ) );
	}

	/**
	 * @param dbUrl
	 */
	public StableConnectionProvider(IConnectionFactory connectionFactory ) {
		super();
		this.connectionFactory = connectionFactory;
	}
	
	private PooledConnection createPooledConnection()
			throws InstantiationConnectionException {
		com.mysql.cj.jdbc.JdbcConnection connection = (com.mysql.cj.jdbc.JdbcConnection)this.connectionFactory.createConnection();
		++ POOLED_CONNECTION_COUNTER;
		return new MysqlPooledConnection(connection);
		//return new MysqlPooledConnection(connection);		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.domainmodel2.db.statements.IConnectionProvider#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		if ( !canResueCurrentPooledConnection() ) {
			closePooledConnection();
			this.pooledConnection = createPooledConnection();
			logger.info("New pooled connection created " + this.pooledConnection);
		} 
		return this.pooledConnection.getConnection();
	}

	/**
	 * 
	 */
	private void closePooledConnection() {
		if (this.pooledConnection != null) {
			try {
				this.pooledConnection.close();
			} catch (SQLException ex) {
				logger.debug("Can't close connection", ex);
			}
			this.pooledConnection = null;
		}
	}

	/**
	 * @return true if connection valid
	 */
	private boolean canResueCurrentPooledConnection() {
		if (this.pooledConnection == null) {
			return false;
		}
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			connection = this.pooledConnection.getConnection();
			if ( connection == null ) {
				return false;
			}
			// // // 
			// Remove this, may be is not necessary.
			// // //
			// statement = connection.createStatement();
			// rs = statement.executeQuery(TEST_QUERY);
			// // //
			return true;
		} catch (SQLException ex) {
			logger.debug("Connection test failed", ex);
		}
		catch(RuntimeException ex ) {
			logger.error("Unexcpected errot in connection test", ex);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					logger.debug("Can't close result set", ex);
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					logger.debug("Can't close statement", ex);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					logger.debug("Can't close connection", ex);
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.domainmodel2.db.statements.IConnectionProvider#dispose()
	 */
	public synchronized void dispose() {
		closePooledConnection();
	}

	/**
	 * @return the POOLED_CONNECTION_COUNTER
	 */
	public static int getPooledConnectionCounter() {
		return POOLED_CONNECTION_COUNTER;
	}
	
}
