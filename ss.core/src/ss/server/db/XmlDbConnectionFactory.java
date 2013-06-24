package ss.server.db;

import java.sql.Connection;
import java.sql.SQLException;


import ss.server.db.DBPool.InstantiationConnectionException;
import ss.server.domainmodel2.db.statements.ConnectionFactory;

public class XmlDbConnectionFactory extends ConnectionFactory {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XmlDbConnectionFactory.class);
	
	/**
	 * @param dbUrl
	 */
	public XmlDbConnectionFactory(String dbUrl) {
		super(dbUrl);
	}

	@Override
	public Connection createConnection() throws InstantiationConnectionException {
		Connection connection = super.createConnection();
		try {
			connection.setAutoCommit( true );
		} catch (SQLException ex) {
			logger.error( "Can't set up connection", ex );
		}
		return connection;
	}

	
}
