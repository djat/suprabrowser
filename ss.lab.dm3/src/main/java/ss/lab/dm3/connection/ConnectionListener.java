package ss.lab.dm3.connection;

/**
 * @author Dmitry Goncharov
 */
public interface ConnectionListener {

	void beforeConnectionClosed( Connection connection );
	
}
