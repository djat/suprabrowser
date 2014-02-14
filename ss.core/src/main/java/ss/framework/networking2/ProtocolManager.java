/**
 * 
 */
package ss.framework.networking2;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ss.framework.networking2.indexing.ProtocolIndex;
import ss.framework.networking2.properties.ProtocolProperties;

/**
 *
 */
public abstract class ProtocolManager {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ProtocolManager.class);
	
	private final Hashtable<ProtocolProperties, Protocol > identityToProtocol = new Hashtable<ProtocolProperties, Protocol >();
	
	private final ProtocolIndex index = new ProtocolIndex();
	
	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	/**
	 * 
	 * @param identity
	 * @return
	 */
	public final synchronized boolean exists( ProtocolProperties identity ) {
		this.readWriteLock.readLock().lock();
		try {
			return this.identityToProtocol.contains(identity );
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}
	
	/**
	 * Returns protocol by identinty
	 * @param identity
	 * @return
	 */
	public final synchronized Protocol requireProtocol( ProtocolProperties identity ) {
		this.readWriteLock.readLock().lock();
		try {
			Protocol protocol = this.identityToProtocol.get(identity);
			if (protocol == null) {
				throw new IllegalArgumentException(
						"Protocol not found. Identity " + identity);
			}
			return protocol;
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}	
	
	/**
	 * @param protocol
	 */
	final synchronized void protocolBeginClose(Protocol protocol) {
		this.readWriteLock.writeLock().lock();
		try {
			final ProtocolProperties identity = protocol.getProperties();
			if (identity != null) {
				this.index.remove(protocol);
				this.identityToProtocol.remove(identity);
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		}		
	}

	/**
	 * @param protocol
	 */
	final synchronized void protocolStarted(Protocol protocol) {
		this.readWriteLock.writeLock().lock();
		try {
			final ProtocolProperties identity = protocol.getProperties();
			if (exists(identity)) {
				throw new IllegalStateException(
						"Cannot add protocol, because protocol with same id already exists. Existed "
								+ requireProtocol(identity));
			}
			this.identityToProtocol.put(identity, protocol);
			this.index.add(protocol);
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}
	
	/**
	 * 
	 */
	public final synchronized void beginCloseAll() {
		this.readWriteLock.writeLock().lock();
		try {
			ArrayList<Protocol> protocolsToStop = new ArrayList<Protocol>(
					this.identityToProtocol.values());
			for (Protocol protocol : protocolsToStop) {
				protocol.beginClose();
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		}		
	}

	/**
	 * @return the index
	 */
	public final ProtocolIndex getIndex() {
		return this.index;
	} 


}
