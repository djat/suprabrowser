package ss.server.domainmodel2.db.statements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ss.server.db.DBPool.DriverNotCreatedException;
import ss.server.db.DBPool.InstantiationConnectionException;

public class ConnectionFactory implements IConnectionFactory {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ConnectionFactory.class);
	
	private static boolean DRIVER_INITIALIZED = false;
	
	private static final String JDBC_DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
	
	private final String dbUrl;
	
	/**
	 * @param dbUrl
	 */
	public ConnectionFactory(final String dbUrl) {
		super();
		this.dbUrl = dbUrl;
	}


	private void instantDriver() throws DriverNotCreatedException {
		try {
			Class.forName(JDBC_DRIVER_NAME).newInstance();
		} catch (InstantiationException e) {
			String message = "Could not create driver instance";
			logger.error(message, e);
			throw new DriverNotCreatedException(message, e);
		} catch (IllegalAccessException e) {
			String message = "Driver default constructor not visible";
			logger.error(message, e);
			throw new DriverNotCreatedException(message, e);
		} catch (ClassNotFoundException e) {
			String message = "Driver Class not found";
			logger.error(message, e);
			throw new DriverNotCreatedException(message, e);
		}
	}


	/* (non-Javadoc)
	 * @see ss.server.domainmodel2.db.statements.IConnectionFactory#createConnection()
	 */
	public Connection createConnection() throws InstantiationConnectionException {
		if (!DRIVER_INITIALIZED) {
			instantDriver();
			DRIVER_INITIALIZED = true;
		}		
		try {
			return DriverManager.getConnection(this.dbUrl);
		} catch (SQLException ex) {
			String message = "Could not instant connection : " + this.dbUrl;
			logger.error(message, ex);
			throw new InstantiationConnectionException( message, ex );
		}
	}
	

}
