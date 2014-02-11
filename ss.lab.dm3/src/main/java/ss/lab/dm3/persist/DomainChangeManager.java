package ss.lab.dm3.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ss.lab.dm3.persist.changeset.CrudSet;

public class DomainChangeManager {

	private final RepositoryListener repositoryListener = new RepositoryObserver();

	private final Repository repository;

	private final List<HashMap<?,?>> readOnlyUsedListenerToFilters = new ArrayList<HashMap<?,?>>(); 
	
	private HashMap<DomainChangeListener, FilteredListener> sharedListenerToFilter = new HashMap<DomainChangeListener, FilteredListener>();

	/**
	 * @param domain
	 */
	public DomainChangeManager(Repository repository) {
		super();
		this.repository = repository;
		this.repository.addRepositoryListener(this.repositoryListener);
	}

	/**
	 * @param changeSet
	 */
	protected void domainChanged(CrudSet crudSet) {
		final HashMap<DomainChangeListener, FilteredListener> listenerToFilter;
		synchronized( this ) {
			listenerToFilter = this.sharedListenerToFilter;
			this.readOnlyUsedListenerToFilters.add( listenerToFilter );
		}
		try {
			for (FilteredListener filtered : listenerToFilter.values() ) {
				filtered.domainChanged(crudSet);
			}
		}
		finally {
			synchronized( this ) {
				this.readOnlyUsedListenerToFilters.remove( listenerToFilter );	
			}
		}
	}
	
	
	public synchronized void addListener(DomainChangeListener listener, Class<? extends DomainObject> objectClass ) {
		final HashMap<DomainChangeListener, FilteredListener> modificableListenerToFilter = this.getModificableListenerToFilter();
		FilteredListener filtered = modificableListenerToFilter.get( listener );
		if ( filtered == null ) {
			filtered = new FilteredListener( listener );
			modificableListenerToFilter.put( listener, filtered );
		}
		filtered.add(objectClass);	
	}
	
	/**
	 * @param listener
	 * @param involvedClasses
	 */
	public synchronized void addListener(DomainChangeListener listener,
			Class<? extends DomainObject>[] involvedClasses) {
		for( Class<? extends DomainObject> objectClass : involvedClasses ) {
			addListener(listener, objectClass );
		}
	}
	
	public synchronized void removeListener(DomainChangeListener listener, Class<? extends DomainObject> objectClass ) {
		final HashMap<DomainChangeListener, FilteredListener> modificableListenerToFilter = this.getModificableListenerToFilter();
		FilteredListener filtered = modificableListenerToFilter.get( listener );
		if ( filtered != null ) {
			filtered.remove(objectClass);
			if ( filtered.isEmpty() ) {
				modificableListenerToFilter.remove( listener );
			}
		}
	}

	public synchronized void removeListener(DomainChangeListener listener) {
		this.getModificableListenerToFilter().remove(listener);
	}
	
	public synchronized void dispose() {
		if (this.repository != null) {
			this.repository.removeRepositoryListener(this.repositoryListener);
		}
		this.getModificableListenerToFilter().clear();
	}

	/**
	 * @return the listenerToFilter
	 */
	private synchronized HashMap<DomainChangeListener, FilteredListener> getModificableListenerToFilter() {
		if ( this.readOnlyUsedListenerToFilters.contains( this.sharedListenerToFilter ) ) {
			// Create another copy of shared to avoid modification exception in read only interation 
			this.sharedListenerToFilter = new HashMap<DomainChangeListener, FilteredListener>( this.sharedListenerToFilter );
		}
		return this.sharedListenerToFilter;
	}

	private static class FilteredListener {

		private final DomainChangeListener listener;
		
		private Set<Class<? extends DomainObject>> filteredClasses = new HashSet<Class<? extends DomainObject>>();

		/**
		 * @param listener
		 */
		public FilteredListener(DomainChangeListener listener) {
			super();
			this.listener = listener;
		}

		/**
		 * @return
		 */
		public boolean isEmpty() {
			return this.filteredClasses.isEmpty();
		}

		public void domainChanged(CrudSet crudSet) {
			if (match(crudSet)) {
				this.listener.domainChanged(crudSet);
			}
		}

		private boolean match(CrudSet crudSet) {
			for (Class<? extends DomainObject> objClazz : this.filteredClasses) {
				if (crudSet.contains(objClazz)) {
					return true;
				}
			}
			return false;
		}

		public void add(Class<? extends DomainObject> objectClazz) {
			this.filteredClasses.add( objectClazz);
		}
		
		public void remove(Class<? extends DomainObject> objectClazz) {
			this.filteredClasses.remove( objectClazz);
		}
	}

	private final class RepositoryObserver extends RepositoryAdapter {
		@Override
		public void repositoryChanged(CrudSet crudSet) {
			domainChanged(crudSet);
		}
	}

	

}
