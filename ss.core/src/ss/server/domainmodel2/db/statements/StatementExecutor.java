/**
 * 
 */
package ss.server.domainmodel2.db.statements;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import ss.common.ArgumentNullPointerException;
import ss.common.IdentityUtils;
import ss.server.domainmodel2.db.IResultSetRowHandler;
import ss.server.domainmodel2.db.IStatementExecutor;
import ss.server.domainmodel2.db.ResultSetRowHandlerException;

/**
 * 
 */
final class StatementExecutor implements IStatementExecutor {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(StatementExecutor.class);

	final StatementExecutorPool poolOwner;

	private Thread statementThread = null;

	private Connection connection = null;

	private final IStableConnectionProvider connectionProvider; 
	
	private final AtomicBoolean inTransaction = new AtomicBoolean(false);

	private final String id = IdentityUtils
			.getNextRuntimeId(StatementExecutor.class);

	StatementExecutor(StatementExecutorPool statementExecutorPool, String dbUrl ) {
		this( statementExecutorPool, new StableConnectionProvider( dbUrl ) );
	}
	
	/**
	 * @param dbUrl
	 */
	private StatementExecutor(StatementExecutorPool statementExecutorPool, IStableConnectionProvider connectionProvider) {
		super();
		this.poolOwner = statementExecutorPool;
		if ( connectionProvider == null ) {
			throw new ArgumentNullPointerException( "connectionProvider" );
		}
		this.connectionProvider = connectionProvider;
		logger.info("new statement executor created " + this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.domainmodel2.db.IStatementExecutor#execute(java.lang.String)
	 */
	public void execute(String query) {
		checkAccessibility();
		if ( !isInTransaction() ) {
			throw new NotInTransactionException( query );
		}
		Statement statement = null;
		try {
			statement = this.connection.createStatement();
			boolean result = statement.execute(query);
			if (logger.isDebugEnabled()) {
				logger.debug("Execute "
						+ query
						+ ", result "
						+ (result ? "true" : "rows "
								+ statement.getUpdateCount()));
			}
		} catch (SQLException ex) {
			throw new QueryFailedException(query, ex);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					logger.error("Can't close statement", ex);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.domainmodel2.db.IStatementExecutor#executeQuery(java.lang.String,
	 *      ss.server.domainmodel2.db.IRecordCollector)
	 */
	public void executeQuery(String query, IResultSetRowHandler recordCollector) {
		checkAccessibility();
		ResultSet rs = null;
		Statement statement = null;
		try {
			statement = this.connection.createStatement();
			rs = statement.executeQuery(query);
			int count = 0;
			while (rs.next()) {
				recordCollector.handleRow(rs);
				++count;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Rows (" + count + ") selected by >> " + query);
			}
		} catch (SQLException ex) {
			throw new QueryFailedException(query, ex);
		} catch (ResultSetRowHandlerException ex) {
			throw new QueryFailedException(query, ex.getRealException());
		} finally {
			if ( rs != null ) {
				try {
					rs.close();					
				}
				catch (SQLException ex) {
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
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.domainmodel2.db.IStatementExecutor#commit()
	 */
	public void commitTransaction() {
		checkAccessibility();
		try {
			this.connection.commit();
		} catch (SQLException ex) {
			throw new CommitFailedException(ex);
		} finally {
			resetInTransaction();
		}
	}

	/**
	 */
	private void resetInTransaction() {
		this.inTransaction.set(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.domainmodel2.db.IStatementExecutor#disposeTransaction()
	 */
	public void disposeTransaction() {
		checkAccessibility();
		try {
			if (isInTransaction()) {
				try {
					this.connection.rollback();
				} catch (SQLException ex) {
					throw new RollbackFailedException(ex);
				}
			}
		} finally {
			resetInTransaction();
		}
	}

	/**
	 * @return
	 */
	private boolean isInTransaction() {
		return this.inTransaction.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.domainmodel2.db.IStatementExecutor#openTransaction()
	 */
	public void openTransaction() {
		checkAccessibility();
		disposeTransaction();
		this.inTransaction.set(true);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.domainmodel2.db.IStatementExecutor#release()
	 */
	public void release() {
		checkAccessibility();
		try {
			disposeTransaction();
		}
		finally {
			if ( this.connection != null ) {
				try {
					this.connection.close();
				} catch (SQLException ex) {
					logger.warn( "Can't close connection", ex );
				}
			}
			this.connection = null;
			this.statementThread = null;
			if (this.poolOwner != null) {
				this.poolOwner.release(this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.statementThread != null) {
			return this.id + " allocated for " + this.statementThread;
		} else {
			return this.id;
		}
	}

	private void checkAccessibility() {
		if (this.poolOwner != null) {
			if (!isInUse()) {
				throw new NotInUseException(this);
			}
			Thread current = Thread.currentThread();
			if (this.statementThread != current) {
				throw new IllegalThreadAccessException(current, this);
			}
		}
	}

	/**
	 * 
	 */
	void dispose() {
		if ( isInUse() ) {
			logger.error( "Statement executor in use " + this );
		}
		this.connectionProvider.dispose();
	}

	/**
	 * 
	 */
	private void releaseConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException ex) {
				logger.error("Cannot close connection", ex);
			}
			this.connection = null;
		}
	}
	
	/**
	 * @return
	 */
	private boolean isInUse() {
		return this.statementThread != null;
	}

	void use() {
		if ( isInUse() ) {
			throw new AlreadyInUseException( this );
		}
		this.statementThread = Thread.currentThread();
		try {
			this.connection = this.connectionProvider.getConnection();
			this.connection.setAutoCommit(false);
			this.connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		}
		catch (SQLException ex) {
			releaseConnection();
			throw new CannotSetUpConnection( ex );
		}
	}
	
	/**
	 * 
	 */
	public static class CommitFailedException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5173255197263214409L;

		/**
		 * @param string
		 * @param ex
		 */
		public CommitFailedException(SQLException ex) {
			super("Commit failed", ex);
		}

	}

	/**
	 * 
	 */
	public static class RollbackFailedException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5511383380751030034L;

		/**
		 * @param string
		 * @param ex
		 */
		public RollbackFailedException(SQLException ex) {
			super("Rollback failed", ex);
		}

	}

	/**
	 * 
	 */
	public static class QueryFailedException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6799977815310801404L;

		/**
		 * @param string
		 * @param cause
		 */
		public QueryFailedException(String query, Throwable cause) {
			super("Query failed >> " + query, cause);
		}

	}
	
	/**
	 * 
	 */
	public static class NotInTransactionException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2918340420366955099L;

		/**
		 * @param query
		 */
		public NotInTransactionException(String query) {
			super("Object not in transaction. Query >> " + query);
		}
	}
	

	/**
	 *
	 */
	public static final class AlreadyInUseException extends IllegalStateException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2915521792910762692L;

		/**
		 * @param executor
		 */
		public AlreadyInUseException(StatementExecutor executor) {
			super( "Statement already in use " + executor );
		}

	}	
}
