/**
 * 
 */
package ss.server.domainmodel2.db;

import ss.common.FileUtils;
import ss.server.db.DbUrlProvider;
import ss.server.domainmodel2.db.statements.StatementExecutorPool;

/**
 *
 */
public final class TableStructureManager {

	/**
	 * 
	 */
	private static final String DM2_CREATE_TABLES_SQL = "dm2_create_tables.sql";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TableStructureManager.class);
	
	/**
	 * Singleton instance
	 */
	public final static TableStructureManager INSTANCE = new TableStructureManager();

	private TableStructureManager() {
	}
	
	public static void main(String[] args) {
		try {
			INSTANCE.recreateDm2Tables();
		}
		catch( Exception ex ) {
			logger.fatal( "Can't recreate table structure", ex );
		}
	}

	/**
	 * 
	 */
	public void recreateDm2Tables() {
		system_executeBatch(TableStructureManager.class, DM2_CREATE_TABLES_SQL);
	}

	/**
	 * @param clazz
	 * @param relativeFileName
	 */
	public void system_executeBatch(Class clazz, String relativeFileName) {
		logger.warn( "Recreating tables .... by " + clazz.getPackage().getName() + " | " + relativeFileName );
		system_executeBatch( FileUtils.loadText( clazz, relativeFileName ) );
		logger.warn( "Done" );
	}
	
	public static void system_executeBatch(String script) {
		StatementExecutorPool pool = new StatementExecutorPool( DbUrlProvider.INSTANCE.getDbUrl());
		IStatementExecutor statementExecutor = pool.getStatementExecutor();
		try {
			statementExecutor.openTransaction();
			String[] queries = script.split(";");
			for (String query : queries) {
				final String normalized = query.trim();
				if (normalized.length() > 0) {
					statementExecutor.execute(normalized);
				}
			}
			statementExecutor.commitTransaction();
		} finally {
			statementExecutor.release();
		}
	}

	
	
	
}
