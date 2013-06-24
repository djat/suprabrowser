/**
 * 
 */
package ss.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import ss.server.domainmodel2.db.IResultSetRowHandler;
import ss.server.domainmodel2.db.ResultSetRowHandlerException;
import ss.server.domainmodel2.db.statements.IStableConnectionProvider;

/**
 * @author zobo
 * 
 */
public class QueryExecutor {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(QueryExecutor.class);

	/**
	 * @param query
	 * @return
	 * @throws SQLException
	 * @throws DocumentException
	 */
	private static Document queryFirstDocument2(final String query)
			throws ResultSetRowHandlerException, SQLException {
		final AtomicReference<Document> doc = new AtomicReference<Document>();
		executeQuery(query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				String xml = rs.getString(1);
				try {
					doc.set(DocumentHelper.parseText(xml));
				} catch (DocumentException ex) {
					logger.error("Parse failed. Xml " + xml);
					throw new ResultSetRowHandlerException(ex);
				}
			}
		});
		return doc.get();
	}

	private static void executeQuery(String query, IResultSetRowHandler recordCollector)
			throws ResultSetRowHandlerException, SQLException {
		final IStableConnectionProvider connectionProvider = DBPool.bind();
		Connection connection = null;
		java.sql.Statement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionProvider.getConnection();
			statement = connection.createStatement();
			statement.getConnection();
			rs = statement.executeQuery(query);
			int count = 0;
			while (rs.next()) {
				recordCollector.handleRow(rs);
				++count;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Rows (" + count + ") selected by >> " + query);
			}
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					logger.error("Can't close result set", ex);
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					logger.error("Can't close statement", ex);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					logger.error("Can't close connection", ex);
				}
			}
			DBPool.unbind(connectionProvider);
		}
	}
	
	public static int executeUpdate(String query) throws SQLException {
		final IStableConnectionProvider connectionProvider = DBPool.bind();
		Connection connection = null;
		java.sql.Statement statement = null;
		try {
			connection = connectionProvider.getConnection();
			statement = connection.createStatement();
			return statement.executeUpdate(query);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					logger.error("Can't close statement", ex);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					logger.error("Can't close connection", ex);
				}
			}
			DBPool.unbind(connectionProvider);
		}
	}
	
	public static boolean executeDelete(String query) throws SQLException {
		final IStableConnectionProvider connectionProvider = DBPool.bind();
		Connection connection = null;
		java.sql.Statement statement = null;
		try {
			connection = connectionProvider.getConnection();
			statement = connection.createStatement();
			return statement.execute(query);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					logger.error("Can't close statement", ex);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					logger.error("Can't close connection", ex);
				}
			}
			DBPool.unbind(connectionProvider);
		}
	}
	
	/**
	 * @param query
	 * @param handler
	 */
	public static void safeExecuteQuery(String query, IResultSetRowHandler handler) {
		try {
			executeQuery(query, handler);
		} catch (ResultSetRowHandlerException ex) {
			logger.error( "ResultSet handler failed. Query " + query + ". Handler " + handler, ex);
		} catch (SQLException ex) {
			logger.error( "ResultSet handler failed. Query " + query + ". Handler " + handler, ex);
		}
	}
	
	/**
	 * @param query
	 * @return
	 */
	public static Document safeQueryFirstDocument(String query) {
		try {
			return queryFirstDocument2(query);
		} catch (SQLException ex) {
			logger.error("Query first document failed " + query, ex);
		} catch (ResultSetRowHandlerException ex) {
			logger.error("Query first document failed " + query, ex
					.getRealException());
		}
		return null;
	}
	
	public static int safeExecuteUpdate(String query) {
		try {
			return executeUpdate(query);
		} catch (SQLException ex) {
			logger.error("execute update failed: " + query, ex);
			return 0;
		}
	}
}
