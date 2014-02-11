package ss.lab.dm3.persist.synclist;

import java.util.Hashtable;
import java.util.List;

import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.Repository;

/**
 * @author Dmitry Goncharov
 */
public class SynchronizedListProviderManager {

	private final Repository repository;
	
	private final Hashtable<TypedQuery<?>,SynchronizedListProvider<?>> criteriaToProvider = new Hashtable<TypedQuery<?>, SynchronizedListProvider<?>>();
	
	private final SynchronizedListProviderIndex byClassIndex = new SynchronizedListProviderIndex(); 
	
	/**
	 * @param repository
	 */
	public SynchronizedListProviderManager(Repository repository) {
		super();
		this.repository = repository;
	}

	public <T extends DomainObject> SynchronizedListProvider<T> get(TypedQuery<T> fetchCriteria) {
		SynchronizedListProvider<T> provider = find(fetchCriteria);
		if ( provider == null ) {
			provider = create(fetchCriteria);
			this.criteriaToProvider.put( provider.getFetchCriteria(), provider );
			this.byClassIndex.add(provider);
		}
		return provider;
	}

	private <T extends DomainObject> SynchronizedListProvider<T> create(TypedQuery<T> fetchCriteria) {
		return new SynchronizedListProvider<T>( this.repository, fetchCriteria );
	}
	
	@SuppressWarnings("unchecked")
	private <T extends DomainObject> SynchronizedListProvider<T> find(TypedQuery<T> fetchCriteria) {
		return (SynchronizedListProvider) this.criteriaToProvider.get( fetchCriteria );
	}
	
	public void objectUnloaded( DomainObject object ) {
		// TODO implement provider releasing 
	}

	@SuppressWarnings("unchecked")
	public void afterObjectChanged( DomainObject object ) {
		afterObjectChanged( (Class)object.getEntityClass(), object );
	}
	
	/**
	 * @param objClazz
	 */
	private <T extends DomainObject> void afterObjectChanged( Class<T> objClazz, T object ) {
		List<SynchronizedListProvider<T>> providers = this.byClassIndex.findByClass( objClazz );
		if ( providers != null ) {
			for( SynchronizedListProvider<T> provider : providers ) {
				provider.afterObjectChanged(object);
			}
		}
	}


	protected void release( SynchronizedListProvider<?> provider ) {
		this.criteriaToProvider.remove( provider.getFetchCriteria() );
		this.byClassIndex.remove( provider );
	}
}
