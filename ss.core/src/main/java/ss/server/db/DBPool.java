package ss.server.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ss.server.domainmodel2.db.statements.ConnectionFactory;
import ss.server.domainmodel2.db.statements.StableConnectionProvider;
import ss.server.domainmodel2.db.statements.IStableConnectionProvider;

public class DBPool {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DBPool.class);
	
    private static final AtomicReference<DBPool> pool = new AtomicReference<DBPool>(null);
    
    private String dbURL = null;
    
    private final List<IStableConnectionProvider> freeConn = new ArrayList<IStableConnectionProvider>(40);
    
    private final List<IStableConnectionProvider> usedConn = new ArrayList<IStableConnectionProvider>(40);

    private DBPool() {
    	loadDBURL();
    }

    @Override
    protected void finalize() throws Throwable {
    	checkState();
    	super.finalize();
    }
    
    private IStableConnectionProvider bindConnection() {
    	synchronized (getSyncRoot()) {
    		if (this.freeConn.size() < 1) {
    			this.freeConn.add(createConnectionProvider());
    		}
		    int index = this.freeConn.size() - 1;
		    IStableConnectionProvider connection = this.freeConn.get(index);
		    this.freeConn.remove(index);
		    this.usedConn.add(connection);
		    return connection;
    	}
    }

    private void checkState() {
		int usedSize = this.usedConn.size();
		if (usedSize > 0) {
		    logger.error("There is " + usedSize + " connection"
			    + (usedSize > 1 ? "s" : "") + " not unbinded!");
		} else {
		    logger.info("All OK");
		}
    }

    private IStableConnectionProvider createConnectionProvider()
	    throws InstantiationConnectionException {
    	return new StableConnectionProvider( new ConnectionFactory( this.dbURL ) );	    
    }

    private void freeAll() {
    	synchronized (getSyncRoot()) {
    		for (IStableConnectionProvider connectionProvider : this.freeConn) {
    			connectionProvider.dispose();
    		}
    		this.freeConn.clear();
    		for (IStableConnectionProvider connectionProvider : this.usedConn) {
    			connectionProvider.dispose();
    		}
    		this.usedConn.clear();
		}
    }

	private void loadDBURL() {
	    this.dbURL = DbUrlProvider.INSTANCE.getDbUrl();
	}

    private void unbindConnection(IStableConnectionProvider connection) {
		synchronized (getSyncRoot()) {
		    boolean unbined = this.usedConn.remove(connection);
		    if (!unbined) {
			throw new NoSuchConnectionException(
				"unBinded connection was not managed by this instance. Details:"
					+ connection.toString());
		    }
		    this.freeConn.add(connection);
		}
    }

    /**
     * @return return binded db connection
     */
    public static IStableConnectionProvider bind() {
    	return getPool().bindConnection();
    }

    public static DBPool getPool() {
    	if (pool.get() == null) {
			pool.compareAndSet(null, new DBPool());
		}
		return pool.get();
	}

    public static void recreate() {
		DBPool oldPool = getPool();
		oldPool.checkState();
		oldPool.freeAll();
		pool.set(new DBPool());
    }

    /**
	 * @param connection
	 *            returned connection to pool so it can be reused later
	 */
	public static void unbind(IStableConnectionProvider connection) {
		getPool().unbindConnection(connection);
	}

    /**
	 * @return
	 */
	private Object getSyncRoot() {
		return this.freeConn;
	}
	
	/**
	 * 
	 */
	public int freeConnSize() {
		return this.freeConn.size();
	}

	/**
	 * 
	 */
	public int usedConnSize() {
		return this.usedConn.size();
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    public static class DriverNotCreatedException extends RuntimeException {

    	/**
		 * 
		 */
		private static final long serialVersionUID = -4660705850104259351L;

		public DriverNotCreatedException(String message, Throwable cause) {
    	    super(message, cause);
    	}

        }

        public static class InstantiationConnectionException extends RuntimeException {

    	/**
			 * 
			 */
			private static final long serialVersionUID = -6264314964673045752L;

		public InstantiationConnectionException(String message, Throwable cause) {
    	    super(message, cause);
    	}

        }

        public static class NoSuchConnectionException extends RuntimeException {

    	/**
			 * 
			 */
			private static final long serialVersionUID = 7686606954009451997L;

		public NoSuchConnectionException(String message) {
    	    super(message);
    	}

        }
}
