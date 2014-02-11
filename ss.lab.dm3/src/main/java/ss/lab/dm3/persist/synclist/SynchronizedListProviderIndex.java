package ss.lab.dm3.persist.synclist;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ss.lab.dm3.persist.DomainObject;

/**
 * @author Dmitry Goncharov
 */
public class SynchronizedListProviderIndex {

	private final Hashtable<Class<?>, List<?>> classToProviders = new Hashtable<Class<?>, List<?>>();
	
	public <T extends DomainObject> void add( SynchronizedListProvider<T> provider ) {
		final Class<T> entityClass = provider.getEntityClass();
		List<SynchronizedListProvider<T>> list = findByClass( entityClass );
		if ( list ==  null ) {
			list = new ArrayList<SynchronizedListProvider<T>>();
			this.classToProviders.put( entityClass, list );
		}
		list.add( provider ); 
	}
	
	@SuppressWarnings("unchecked")
	public <T extends DomainObject> List<SynchronizedListProvider<T>> findByClass(Class<T> objClazz) {
		return (List) this.classToProviders.get(objClazz);
	}

	/**
	 * @param provider
	 */
	public <T extends DomainObject> void remove(SynchronizedListProvider<T> provider) {
		final Class<T> entityClass = provider.getEntityClass();
		List<SynchronizedListProvider<T>> providers = findByClass( entityClass );
		if ( providers != null ) {
			providers.remove( provider );
			if ( providers.size() == 0 ) {
				this.classToProviders.remove( entityClass );
			}
		}
	}
}
