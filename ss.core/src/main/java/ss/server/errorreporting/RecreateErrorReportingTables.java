package ss.server.errorreporting;

import ss.global.SSLogger;
import ss.global.LoggerConfiguration;
import ss.server.domainmodel2.db.TableStructureManager;

public class RecreateErrorReportingTables {

	/**
	 * 
	 */
	private static final String ERROR_REPORTING_CREATE_TABLES_SQL = "error_reporting_create_tables.sql";

	public static void main(String[] args) {
		SSLogger.initialize( LoggerConfiguration.DEFAULT );
		recreateErrorReportingTables();
	}

	/**
	 * 
	 */
	public static void recreateErrorReportingTables() {
		TableStructureManager.INSTANCE.system_executeBatch( RecreateErrorReportingTables.class, ERROR_REPORTING_CREATE_TABLES_SQL );
	}
}
