/**
 * 
 */
package ss.server.errorreporting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import ss.server.domainmodel2.db.statements.StableConnectionProvider;

/**
 *
 */
public class PreparedStatementHelper {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreparedStatementHelper.class);
	
	private final String query;
	
	private final StableConnectionProvider connectionProvider;
	
	private Connection connection;
	
	private PreparedStatement statement;

	private boolean statementCreated = false;
	
	private boolean connectionCreated = false;

	/**
	 * @param query
	 * @param connectionProvider
	 */
	public PreparedStatementHelper(String query, StableConnectionProvider connectionProvider) {
		this.query = query;
		this.connectionProvider = connectionProvider;
	}

	/**
	 * 
	 */
	public void silentClose() {
		if ( this.statement != null) {
			try {
				this.statement.close();
			} catch (SQLException ex) {
				logger.error("Can't close statement", ex);
			}
		}
		if ( this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException ex) {
				logger.error("Can't close connection", ex);
			}
		}		
	}

	/**
	 * @throws SQLException 
	 * 
	 */
	public void executeAndCommit() throws SQLException {
		getStatement().execute();
		getConnection().commit();
	}

	/**
	 * 
	 */
	public void silentRollback() {
		if ( this.connection != null ) {
			try {
				getConnection().rollback();
			} catch (SQLException rollBackEx) {
				logger.error( "Can't rollback changes", rollBackEx );
			}
		}
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		this.getStatement().setLong(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		this.getStatement().setString(parameterIndex, x);
	}

	/**
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.getStatement().setTimestamp(parameterIndex, x);
	}
	/**
	 * @return the connection
	 * @throws SQLException 
	 */
	private Connection getConnection() throws SQLException {
		if ( !this.connectionCreated ) {
			this.connectionCreated = true;
			this.connection = this.connectionProvider.getConnection();
			if ( this.connection.getAutoCommit() ) {
				this.connection.setAutoCommit( false );
			}
		}
		return this.connection;
	}

	/**
	 * @return the statement
	 * @throws SQLException 
	 */
	private PreparedStatement getStatement() throws SQLException {
		if ( !this.statementCreated ) {
			this.statementCreated = true;
			Connection connection = getConnection();
			this.statement = connection.prepareStatement(this.query); 
		}
		return this.statement;
	}

	/**
	 * @throws SQLException 
	 * 
	 */
	public long executeInsert() throws SQLException  {
		executeAndCommit();
		return getLastInserId();
	}

	/**
	 * @return
	 * @throws SQLException 
	 */
	private long getLastInserId() throws SQLException {
		Statement statement = getConnection().createStatement();
		ResultSet resultSet = null; 
		try {
			resultSet = statement.executeQuery( "select LAST_INSERT_ID()" );
			if ( resultSet.next() ) {
				return resultSet.getLong( 1 );
			}
			else {
				throw new SQLException( "Can't get LAST_INSERT_ID" );
			}
		}
		finally {
			if ( resultSet != null ) {
				try {
					resultSet.close();
				} catch (SQLException ex) {
					logger.error( "Can't close result set",  ex );
				}
			}
			try {
				statement.close();
			} catch (SQLException ex) {
				logger.error( "Can't close statement",  ex );
			}			
		}
	}

}
