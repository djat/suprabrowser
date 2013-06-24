package ss.framework.networking2.indexing;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.LockedIterable;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.properties.ProtocolProperty;

final class ProtocolSamePropertyValueIndex implements IProtocolIndex {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ProtocolSamePropertyValueIndex.class);
	
	private final ProtocolProperty property;
	
	private final ArrayList<Protocol> protocols = new ArrayList<Protocol>();
	
	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	/**
	 * @param property
	 */
	public ProtocolSamePropertyValueIndex(final ProtocolProperty property) {
		super();
		if ( property == null ) {
			throw new ArgumentNullPointerException( "property" );
		}
		this.property = property;
	}

	public final void add(Protocol protocol) {
		this.readWriteLock.writeLock().lock();
		try {
			if (!protocol.getProperties().containsProperty(this.property)) {
				throw new IllegalArgumentException("Protocol has no property "
						+ this.property);
			}
			if (this.protocols.contains(protocol)) {
				throw new IllegalArgumentException(
						"Protocol alerady in index. Property " + this.property);
			} 
			this.protocols.add(protocol);
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}
	
	public final void remove(Protocol protocol) {
		this.readWriteLock.writeLock().lock();
		try {
			if ( !protocol.getProperties().containsProperty( this.property ) ) {
				throw new IllegalArgumentException( "Protocol has no property " + this.property );
			}
			this.protocols.remove( protocol );		
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}
	
	public final LockedIterable<Protocol> lockIterable() {
		if (logger.isDebugEnabled()) {
			logger.debug( "lock iterable " + this.protocols.size() );
		}
		return new LockedIterable<Protocol>(this.protocols, this.readWriteLock.readLock());
	}

	/**
	 * @return
	 */
	public final boolean isEmpty() {
		this.readWriteLock.readLock().lock();
		try {
			return this.protocols.isEmpty();
		} finally {
			this.readWriteLock.readLock().unlock();
		}	
	}
	
}