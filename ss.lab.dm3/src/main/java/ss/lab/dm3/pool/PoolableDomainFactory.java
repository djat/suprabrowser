package ss.lab.dm3.pool;

import static d1.FastAccess.$;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;

import ss.lab.dm3.connection.Connection;
import ss.lab.dm3.connection.SystemConnectionProvider;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainException;

public class PoolableDomainFactory extends BasePoolableObjectFactory {

	protected final Log log = LogFactory.getLog(getClass());
	
	private final SystemConnectionProvider connectionProvider; 
	
	public PoolableDomainFactory(SystemConnectionProvider connectionProvider) {
		super();
		this.connectionProvider = connectionProvider;
	}

	@Override
	public synchronized Object makeObject() throws Exception {
		Connection connection = connectionProvider.create();
		return connection.getDomain();
	}

	@Override
	public void activateObject(Object obj) throws Exception {
		final Domain domain = (Domain) obj;
		try {
			domain.execute( new Runnable() {
				public void run() {
					domain.getRepository().normalizePopulation();
				}
			});
		}
		catch( DomainException ex ) {
			log.error( $("Can't normalize doman {0}", domain), ex );
		}
	}

	@Override
	public void destroyObject(Object obj) throws Exception {
		Domain domain = (Domain) obj;
		if (domain != null ) {
			domain.dispose();
		}
	}

	@Override
	public void passivateObject(Object obj) throws Exception {
		final Domain domain = (Domain) obj;
		try {
			domain.execute( new Runnable() {
				public void run() {
					domain.getRepository().unloadAll();
				}
			});
		}
		catch( DomainException ex ) {
			log.error( $("Can't passivate doman {0}", domain), ex );
		}
	}

	@Override
	public boolean validateObject(Object obj) {
		final Domain domain = (Domain) obj;
		return domain != null && !domain.isDisposed(); //TODO think about locked domains
	}

	public SystemConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	
	
}
