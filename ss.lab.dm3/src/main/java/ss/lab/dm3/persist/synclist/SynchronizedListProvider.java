package ss.lab.dm3.persist.synclist;

import java.util.List;

import ss.lab.dm3.persist.QueryMatcher;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.QueryMatcherFactory;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.Repository;

/**
 * @author Dmitry Goncharov
 */
public class SynchronizedListProvider<T extends DomainObject> {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Repository repository;

	private final TypedQuery<T> fetchCriteria;
	
	private final QueryMatcher queryMatcher;

	private List<T> fetchedItems = null;
	
	/**
	 * @param repository
	 * @param fetchCriteria
	 * @param fetchedItems
	 */
	public SynchronizedListProvider(Repository repository,
			TypedQuery<T> fetchCriteria) {
		super();
		this.repository = repository;
		this.fetchCriteria = fetchCriteria;
		this.queryMatcher = QueryMatcherFactory.INSTANCE.create( fetchCriteria );
	}
	
	public TypedQuery<T> getFetchCriteria() {
		return this.fetchCriteria;
	}

	public Class<T> getEntityClass() {
		return this.fetchCriteria.getEntityClass();
	}
	
	public void afterObjectChanged( T object ) {
		if ( this.fetchedItems == null ) {
			return;
		}
		if ( this.queryMatcher.match( object ) ) {
			if ( this.fetchedItems.contains( object ) ) {
				if ( this.log.isDebugEnabled() ) {
					this.log.debug( "Already have object " + object + " in " + this );
				}
			}
			else {
				this.fetchedItems.add( object );
			}
		}
		else {
			this.fetchedItems.remove( object );
		}
	}
	
	public List<T> get() {
		if (this.fetchedItems == null) {
			this.fetchedItems = this.repository.find(this.fetchCriteria)
					.toList();
		}
		return this.fetchedItems;
	}
}
