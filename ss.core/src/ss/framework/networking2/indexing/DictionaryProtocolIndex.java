package ss.framework.networking2.indexing;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ss.framework.domainmodel2.LockedIterable;
import ss.framework.networking2.Protocol;

abstract class DictionaryProtocolIndex<Key,SubIndex extends IProtocolIndex> implements IProtocolIndex {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DictionaryProtocolIndex.class);
	
	private static final Iterable<Protocol> EMPTY_LIST = new ArrayList<Protocol>();
	
	private final Hashtable<Key,SubIndex> keyToSubindex = new Hashtable<Key, SubIndex>();

	protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	/**
	 * @param key
	 * @param protocol
	 */
	protected final void add(final Key key, Protocol protocol) {
		this.readWriteLock.writeLock().lock();
		try {
			SubIndex subindex = this.keyToSubindex.get(key);
			if (subindex == null) {
				subindex = createSubIndex(key);
				this.keyToSubindex.put(key, subindex);
			}
			subindex.add(protocol);
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}
	
	/**
	 * @param key
	 * @param protocol
	 */
	protected final void remove(final Key key, Protocol protocol) {
		this.readWriteLock.writeLock().lock();
		try {
			IProtocolIndex subindex = this.keyToSubindex.get(key);
			if (subindex == null) {
				logger.error("Protocol subindex was not found by key " + key);
			} else {
				subindex.remove(protocol);
				if (subindex.isEmpty()) {
					this.keyToSubindex.remove(key);
				}
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.indexing.IIndexedSet#isEmpty()
	 */
	public final boolean isEmpty() {
		this.readWriteLock.readLock().lock();
		try {
			return this.keyToSubindex.isEmpty();
		} finally {
			this.readWriteLock.readLock().unlock();
		}	
	}

	/**
	 * @param key
	 * @return
	 */
	protected abstract SubIndex createSubIndex(Key key);

	protected final SubIndex getSubIndex( Key key ) {
		this.readWriteLock.readLock().lock();
		try {
			SubIndex ret = key != null ? this.keyToSubindex.get(key) : null;
			if (logger.isDebugEnabled()) {
				logger.debug( "Getting sub index " + ret + " by " + key );
			}
			return ret;
		} finally {
			this.readWriteLock.readLock().unlock();
		}	
	}
	
	/**
	 * @return
	 */
	protected final LockedIterable<Protocol> lockEmptyIterable() {
		return new LockedIterable<Protocol>( EMPTY_LIST, this.readWriteLock.readLock() );
	}
		
}
