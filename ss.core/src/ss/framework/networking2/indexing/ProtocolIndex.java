package ss.framework.networking2.indexing;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.LockedIterable;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.properties.ProtocolProperty;

public final class ProtocolIndex extends DictionaryProtocolIndex<Class,ProtocolSamePropertyClassIndex> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ProtocolIndex.class);
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.indexing.AbstractProtocolIndexer#createIndexedSet(java.lang.Object)
	 */
	@Override
	protected ProtocolSamePropertyClassIndex createSubIndex(Class key) {
		return new ProtocolSamePropertyClassIndex( key );
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.indexing.IProtocolIndex#add(ss.common.networking2.Protocol)
	 */
	public void add(Protocol protocol) {
		if (protocol == null) {
			throw new ArgumentNullPointerException("protocol");
		}
		this.readWriteLock.writeLock().lock();
		try {
			for( Class propertyClass : protocol.getProperties().getClassificableProperties() ) {
				add( propertyClass, protocol );
			}
			if ( logger.isDebugEnabled() ) {
				logger.debug( "Add protocol to index. Protocol " + protocol );
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		}		
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.indexing.IProtocolIndex#remove(ss.common.networking2.Protocol)
	 */
	public void remove(Protocol protocol) {
		if (protocol == null) {
			throw new ArgumentNullPointerException("protocol");
		}
		this.readWriteLock.writeLock().lock();
		try {
			for( Class propertyClass : protocol.getProperties().getClassificableProperties() ) {
				remove( propertyClass, protocol );
			}
			if ( logger.isDebugEnabled() ) {
				logger.debug( "Remove protocol from index. Protocol " + protocol );
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		}	
	}

	public LockedIterable<Protocol> select( ProtocolProperty property ) {
		if ( property == null ) {
			throw new ArgumentNullPointerException( "property" );
		}
		this.readWriteLock.readLock().lock();
		try {
			ProtocolSamePropertyClassIndex index = super
					.getSubIndex(property.getClass());
			return index != null ? index.select(property) : lockEmptyIterable();
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}

	
}
