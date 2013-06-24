package ss.server.domainmodel2.db;

public interface IStatementExecutor {

	/**
	 * Execute update
	 * @param query
	 */
	void execute(String query);

	/**
	 * Execute query 
	 * @param query
	 * @param recordCollector
	 */
	void executeQuery(String query,	IResultSetRowHandler recordCollector);

	/**
	 * Commit transaction
	 */
	void commitTransaction();

	/**
	 * If transaction was not commited roolback it.   
	 */
	void disposeTransaction();

	/**
	 * Opens statement executor transaction  
	 */
	void openTransaction();

	/**
	 * Release statement executor. Rolling back transaction if it and not commited opened.
	 */
	void release();	

}