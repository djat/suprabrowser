package ss.lab.dm3.persist.changeset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.DomainObjectCollector;
import ss.lab.dm3.persist.Query;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.QueryMatcherFactory;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.query.TypedQueryMatcher;

/**
 * @author Dmitry Goncharov 
 */
public class TransactionChangeSetList implements Iterable<TransactionChangeSet> {
	
	private final List<TransactionChangeSet> items = new ArrayList<TransactionChangeSet>();
	
	public void add( TransactionChangeSet transactionChangeSet ) {
		this.items.add( transactionChangeSet );
	}
	
	public boolean remove( TransactionChangeSet transactionChangeSet ) {
		return this.items.remove( transactionChangeSet );
	}

	/**
	 * @return
	 */
	public int size() {
		return this.items.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<TransactionChangeSet> iterator() {
		return this.items.iterator();
	}

	public void collect(DomainObjectCollector<?> collector ) {
		if ( size() > 0 ) {
			for( TransactionChangeSet changeSet : this ) {
				changeSet.collect( collector );
			}
		}
	}
	
	public <T extends DomainObject> T resolveOrNull(Class<T> objectClazz, Long id) {
		if ( size() > 0 ) {
			DomainObjectCollector<T> collector = new DomainObjectCollector<T>( QueryHelper.eq( objectClazz, id ) );
			collect(collector);
			return collector.getFirstOrNull();
		}
		else {
			return null;
		}
	}

	/**
	 * @param objectId
	 * @return
	 */
	public boolean isRemoved(QualifiedObjectId<? extends DomainObject> objectId) {
		for( TransactionChangeSet changeSet : this.items ) {
			if ( changeSet.isRemoved( objectId ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param objectClazz
	 * @param id
	 * @return
	 */
	public boolean isRemoved(Class<? extends DomainObject> objectClazz, Long id) {
		for( TransactionChangeSet changeSet : this.items ) {
			if ( changeSet.isRemoved( objectClazz, id ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param resultList
	 * @param query
	 */
	public void applyChangesBy(List<? extends DomainObject> target, Query query) {
		if ( query instanceof TypedQuery ) {
			TypedQuery<?> typedQuery = (TypedQuery<?>) query;
			if ( typedQuery.isEvaluable() ) {
				final TypedQueryMatcher matcher = (TypedQueryMatcher) QueryMatcherFactory.INSTANCE.create(typedQuery);
				matcher.removeUnmatched(target);
				if ( target.size() < typedQuery.getLimitSize() ) {
					// Copy unexisted
					DomainObjectCollector<?> collector = DomainObjectCollector.create( typedQuery );
					collect( collector );				
					collector.copyUnexistedTo( target );
				}
			}
		}
	}

	/**
	 * @param objectId
	 * @return
	 */
	public boolean contains(QualifiedObjectId<? extends DomainObject> objectId) {
		for( TransactionChangeSet changeSet : this.items ) {
			if ( changeSet.contains( objectId ) ) {
				return true;
			}
		}
		return false;
	}
}
