package ss.lab.dm3.pool;

import static d1.FastAccess.$;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import ss.lab.dm3.connection.SystemConnectionProvider;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainException;

public class DomainPool {

	protected final Log log = LogFactory.getLog(getClass());
	
	private final PoolableDomainFactory poolableDomainFactory;
	private final GenericObjectPool impl;
	
	public DomainPool(SystemConnectionProvider systemConnectionProvider) {
		this( new PoolableDomainFactory( systemConnectionProvider ) );
	}
	
	public DomainPool(PoolableDomainFactory poolableDomainFactory) {
		super();
		this.poolableDomainFactory = poolableDomainFactory;
		this.impl = new GenericObjectPool( this.poolableDomainFactory );
		Config conf = new Config();
		this.impl.setConfig( conf );
	}

	public Domain borrowDomain() {
		try {
			return (Domain) this.impl.borrowObject();
		} catch (Exception ex) {
			throw new DomainException( "Can't borrow domain from pool", ex );
		}
	}
	
	public void returnDomain( Domain domain ) {
		try {
			this.impl.returnObject( domain );
		} catch (Exception ex) {
			log.error( $( "Can't return domain {0} to pool", domain ), ex );
		}
	}

	public void dispose() {
		try {
			this.impl.close();
		} catch (Exception ex) {
			log.error( "Can't close pool", ex );
		}
	}

	public PoolableDomainFactory getPoolableDomainFactory() {
		return poolableDomainFactory;
	}
	
	
}
