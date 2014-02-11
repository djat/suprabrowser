package ss.lab.dm3.persist.model;

import java.util.Set;

import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainChangeAdapter;
import ss.lab.dm3.persist.DomainChangeListener;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.DomainResolverHelper;
import ss.lab.dm3.persist.changeset.CrudSet;
import ss.lab.dm3.persist.script.QueryScript;
import ss.lab.dm3.persist.space.QuerySpace;
import ss.lab.dm3.persist.space.Space;
import ss.lab.dm3.utils.ListenerList;

public abstract class AbstractModel {
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final ListenerList<ModelChangedListener> listeners = ListenerList.create(ModelChangedListener.class);

	private Domain domain;

	private boolean disposed = true;

	private final DomainChangeListener domainChangeObserver = new DomainChangeObserver();
	
	private MatchBounds bounds = null;
	
	private QuerySpace space = null;
	
	@Override
	protected void finalize() throws Throwable {
		try {
			dispose();
		}
		catch( RuntimeException ex ) {
			this.log.error( "Can't dispose model " + this, ex );
		}
		super.finalize();
	}

	public final void addModelChangedListener(ModelChangedListener listener) {
		this.listeners.add(listener);
	}
	
	public final void removeModelChangedListener(ModelChangedListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * 
	 */
	public final void initialize() {
		this.domain = DomainResolverHelper.getCurrentDomain();
		this.disposed = false;
		initializing( this.domain );		
	}

	/**
	 * 
	 */
	protected abstract QueryScript createFetchScript();	

	/**
	 * @param domain
	 */
	protected void initializing(Domain domain) {
		updateSpace();
		domain.addListener( this.domainChangeObserver, this.bounds.getInvolvedClasses() );		
	}

	/**
	 * 
	 */
	private void updateSpace() {
		unloadSpace();
		this.bounds = createBounds();
		this.space = new QuerySpace( this.bounds );
	}

	/**
	 * @return
	 */
	private final MatchBounds createBounds() {
		MatchBoundsCollector collector = new MatchBoundsCollector();
		collectMatchBounds( collector );
		if ( collector.isEmpty() ) {
			this.log.warn( "Model has no involved classes " + this );
		}
		return collector.createMatchBounds();
	}

	/**
	 * @return
	 */
	public final Domain getDomain() {
		return this.domain;
	}
	
	private void onDomainChanged(CrudSet changeSet) {
		final Space changeSetSpace = changeSet.getSpace();
		if ( changeSetSpace != null ) {
			if ( changeSetSpace == this.space ) {
				onDataLoaded( changeSet );
			}
			else {
				// Now we skip all change set that comes from external spaces
			}
		}
		else {
			onExternalChanges( changeSet );
		}
	}
	/**
	 * @param changeSet
	 */
	protected void onDataLoaded(CrudSet changeSet) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Data " + changeSet + " loaded to " + this );
		}
		notifyModelChanged(changeSet);
	}

	/**
	 * @param crudSet
	 */
	protected void onExternalChanges(CrudSet changeSet) {
		notifyModelChanged(changeSet);
	}

	/**
	 * @param changeSet
	 */
	protected final void notifyModelChanged(CrudSet changeSet) {
		this.listeners.getNotificator().modelChanged(changeSet);
	}
	
	/**
	 * @param collector
	 */
	protected void collectMatchBounds(MatchBoundsCollector collector) {		
	}
		
	public final void dispose() {
		if ( !this.disposed ) {
			this.disposed = true;
			diposing();
			if ( this.log.isDebugEnabled() ) {
				this.log.debug("Desposing " + this);
			}
		}
	}

	/**
	 * 
	 */
	protected void diposing() {
		if ( this.domain != null ) {
			this.domain.removeListener( this.domainChangeObserver );
		}
	}
	
	protected final void checkNotInitialized() {
		if ( !this.disposed ) {
			throw new IllegalStateException( "Model is initialized " + this );
		}
	}
	
	protected final void checkNotDisposed() {
		if ( this.disposed ) {
			throw new IllegalStateException( "Model is disposed " + this );
		}
	}
		
	public void beginLoad( boolean releaseLoadedObjects, ICallbackHandler callbackHandler ) {
		checkNotDisposed();
		if ( releaseLoadedObjects ) {
			unloadSpace();
		}
		QueryScript queryScript = createFetchScript();
		this.space.beginLoad( this.domain, queryScript, callbackHandler );		
	}

	/**
	 * 
	 */
	private void unloadSpace() {
		if ( this.space != null ) {
			Set<DomainObject> releasedObjects = this.domain.getRepository().removeSpace(this.space);
			if (this.log.isDebugEnabled()) {
				this.log.debug( "Space " + this.space + " release " + releasedObjects.size() );
			}
		}
	}


	private final class DomainChangeObserver extends DomainChangeAdapter {
		@Override
		public void domainChanged(CrudSet changeSet) {
			onDomainChanged( changeSet );
		}
	}
	
}
