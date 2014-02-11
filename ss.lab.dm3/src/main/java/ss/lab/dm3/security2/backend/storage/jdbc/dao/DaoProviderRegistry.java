package ss.lab.dm3.security2.backend.storage.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DaoProviderRegistry {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private ThreadLocal<DaoProvider> threadDaoProvider = new ThreadLocal<DaoProvider>();
	
	private final ComboPooledDataSource cpds;
	
	/**
	 * @param cpds
	 */
	public DaoProviderRegistry(ComboPooledDataSource cpds) {
		this.cpds = cpds;
	}

	/**
	 * 
	 */
	public DaoProvider getCurrentDaoProvider() {
		DaoProvider currentDaoProvider = this.threadDaoProvider.get();
		if ( currentDaoProvider == null ) {
			final Connection connection;
			try {
				connection = this.cpds.getConnection();
			}
			catch (SQLException ex) {
				throw new DaoException( "Can't get connection from " + this.cpds, ex );
			}
			try {
				currentDaoProvider = new DaoProvider( this, connection );
			}
			catch (SQLException ex) {
				this.log.error( "Can't create dao provider", ex );
				try {
					connection.close();
				}
				catch (SQLException closeEx) {
					this.log.error( "Can't close connection " + connection, closeEx );
				}
				
			}
			// Don't call "use" because constructor do all that needed 
			this.threadDaoProvider.set( currentDaoProvider );
		}
		else {
			currentDaoProvider.use();
		}
		return currentDaoProvider;
		
	}
	
	void remove( DaoProvider daoProvider ) {
		if ( this.threadDaoProvider.get() == daoProvider ) {
			this.threadDaoProvider.set( null );
		}
		else {
			throw new IllegalStateException( "Can't reset thread dao provider by " + daoProvider + ". Thread dao provider is " + this.threadDaoProvider.get() );
		}
	}

}
