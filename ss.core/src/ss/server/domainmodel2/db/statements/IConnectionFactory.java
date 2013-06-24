package ss.server.domainmodel2.db.statements;

import java.sql.Connection;

import ss.server.db.DBPool.InstantiationConnectionException;

public interface IConnectionFactory {

	Connection createConnection() throws InstantiationConnectionException;
	
}
