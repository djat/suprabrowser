package ss.framework.networking2.indexing;

import ss.framework.domainmodel2.LockedIterable;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.properties.ProtocolProperty;

final class ProtocolSamePropertyClassIndex extends DictionaryProtocolIndex<ProtocolProperty, ProtocolSamePropertyValueIndex>{

	private final Class propertyClass;
	
	/**
	 * @param propertyClass
	 */
	public ProtocolSamePropertyClassIndex(final Class propertyClass) {
		super();
		this.propertyClass = propertyClass;
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.indexing.AbstractProtocolIndexer#createIndexedSet(java.lang.Object)
	 */
	@Override
	protected ProtocolSamePropertyValueIndex createSubIndex(ProtocolProperty key) {
		return new ProtocolSamePropertyValueIndex( key );
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.indexing.IProtocolIndex#add(ss.common.networking2.Protocol)
	 */
	public void add(Protocol protocol) {
		add( getSubKey(protocol), protocol );		
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.indexing.IProtocolIndex#remove(ss.common.networking2.Protocol)
	 */
	public void remove(Protocol protocol) {
		remove( getSubKey(protocol), protocol );
	}

	/**
	 * @param protocol
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ProtocolProperty getSubKey(Protocol protocol) {
		return protocol.getProperties().requireProperty( this.propertyClass );
	}

	/**
	 * @param property
	 * @return
	 */
	public LockedIterable<Protocol> select(ProtocolProperty property) {
		this.readWriteLock.readLock().lock();
		try {
			ProtocolSamePropertyValueIndex subIndex = getSubIndex(property);
			return subIndex != null ? subIndex.lockIterable()
					: lockEmptyIterable();
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}

	
}
