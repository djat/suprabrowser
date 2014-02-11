package ss.lab.dm3.security2.backend.storage.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import ss.lab.dm3.utils.ReflectionHelper;

public class DaoProvider {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private DaoProviderRegistry daoProviderRegistry;
	
	private int counter;
	
	private HashMap<Class<?>, AbstractDao> classToDao = new HashMap<Class<?>, AbstractDao>();

	private Connection connection;
	
	/**
	 * @param daoProviderRegistry
	 * @throws SQLException 
	 */
	public DaoProvider(DaoProviderRegistry daoProviderRegistry, Connection connection) throws SQLException {
		this.daoProviderRegistry = daoProviderRegistry;
		this.counter = 1;
		this.connection = connection;
		this.connection.setAutoCommit( false );
	}

	public <T extends AbstractDao> T get(Class<T> daoClazz ) {
		checkNotDisposed();
		T dao = daoClazz.cast( this.classToDao.get(daoClazz) );
		if ( dao == null ) {
			dao = ReflectionHelper.create( daoClazz );
			dao.setProvider( this );
			this.classToDao.put( daoClazz, dao );
		}
		return dao;
	}
	
	public void release() {
		checkNotDisposed();
		if ( this.counter == 1 ) {
			commitData();
			dispose();
		}
		-- this.counter;
	}

	/**
	 * 
	 */
	private void commitData() {
		try {
			this.connection.commit();
		}
		catch (SQLException ex) {
			throw new DaoException( "Can't commit data", ex );
		}		
	}

	/**
	 * 
	 */
	private void dispose() {
		checkNotDisposed();
		for( AbstractDao dao : this.classToDao.values() ) {
			dao.dispose();
		}
		this.classToDao.clear();
		if ( this.connection != null ) {
			try {
				this.connection.close();
			}
			catch (SQLException ex) {
				this.log.error( "Can't close connection " + this.connection , ex );
			}
			this.connection = null;
		}
		// Remove provide from registry
		this.daoProviderRegistry.remove(this);
	}

	/**
	 * 
	 */
	private void checkNotDisposed() {
		if ( this.counter <= 0 ) {
			throw new IllegalStateException( "DaoProvider is disposed" );
		}
	}

	/**
	 * 
	 */
	public void use() {
		checkNotDisposed();
		++ this.counter;
	}

	/**
	 * @return
	 */
	public Connection getConnection() {
		checkNotDisposed();
		return this.connection;
	}
}
