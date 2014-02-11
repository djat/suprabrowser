package ss.lab.dm3.persist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;

import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.entity.EntityList;
import ss.lab.dm3.orm.managed.ICollectionAccessor;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.property.Property;
import ss.lab.dm3.persist.backend.EntitiesSelectResult;
import ss.lab.dm3.persist.changeset.ChangeSet;
import ss.lab.dm3.persist.changeset.ChangeSetMergerContext;
import ss.lab.dm3.persist.changeset.CleanObjectsOverwriter;
import ss.lab.dm3.persist.changeset.CrudSet;
import ss.lab.dm3.persist.changeset.DataChangeSet;
import ss.lab.dm3.persist.changeset.TransactionChangeSet;
import ss.lab.dm3.persist.changeset.TransactionChangeSetList;
import ss.lab.dm3.persist.space.ConsumeAllSpace;
import ss.lab.dm3.persist.space.Space;
import ss.lab.dm3.persist.stat.DomainStatistics;
import ss.lab.dm3.persist.synclist.SynchronizedListProvider;
import ss.lab.dm3.persist.synclist.SynchronizedListProviderManager;
import ss.lab.dm3.utils.ListenerList;
import static d1.FastAccess.*;

/**
 * @author Dmitry Goncharov
 */
public class Repository {

	private static final int MAX_POPULATION = 4092;

	private static final boolean EXTRA_CHECKS = true;
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	private final Domain domain;

	private final Mapper<DomainObject> mapper;

	private final TransactionChangeSetList transactionChangeSets = new TransactionChangeSetList();

	private final ListenerList<RepositoryListener> listeners = new ListenerList<RepositoryListener>( RepositoryListener.class );

	private final SynchronizedListProviderManager synchronizedListProviderManager;

	private final OrmManager ormManager;

	private final RepositoryCleanObjects cleanObjects;
	
	private final SpaceManager spaces = new SpaceManager(); 
	
	private final ProxyObjectSet proxyObjectSet = new ProxyObjectSet();
	
	/**
	 * 
	 */
	public Repository(Domain domain, Mapper<DomainObject> mapper ) {
		super();
		this.domain = domain;
		this.mapper = mapper;
		this.ormManager = domain.getOrmManager();
		this.cleanObjects = new RepositoryCleanObjects();
		loadDefaultSpaces();
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Repositoty clean objects is " + this.cleanObjects );
		}
		this.synchronizedListProviderManager = new SynchronizedListProviderManager(this);
	}

	public void addRepositoryListener(RepositoryListener listener) {
		this.listeners.add(listener);
	}

	public void removeRepositoryListener(RepositoryListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * TODO May be add transaction affecting to find result? 
	 * @param <T>
	 * @param query
	 * @return
	 */
	public <T extends DomainObject> DomainObjectCollector<T> find(
			TypedQuery<T> query) {
		DomainObjectCollector<T> collector = new DomainObjectCollector<T>( query );
		this.cleanObjects.collect( collector );
		searchInTransactionChangeSets( collector );
		if (this.log.isDebugEnabled()) {
			this.log.debug("Find result is " + collector );
		}
		return collector;
	}
	
	/**
	 * TODG Improve merge of transaction changes and server result.
	 * 
	 * @param collector
	 */
	private void searchInTransactionChangeSets( DomainObjectCollector<?> collector ) {
		this.transactionChangeSets.collect( collector );
	}

	/**
	 * If object was deleted what should return resolve?
	 */
	public <T extends DomainObject> T resolveOrNull(Class<T> objectClazz, Long id) {
		if ( id == null ) {
			return null;
		}
		if ( objectClazz == null ) {
			throw new IllegalArgumentException( "entityClass is null but id is not '" + id + "'" );
		}
		if ( DomainObjectInterceptor.isGeneratedClass( objectClazz ) ) {
			throw new IllegalArgumentException( "Ivalid enitity class " + objectClazz + "." );
		}
		// 1. Look up in clean
		T resolved = this.cleanObjects.resolveOrNull(objectClazz, id);
		if ( resolved != null ) {
			return resolved;
		}  
		// 2. Look up in proxies
		resolved = this.proxyObjectSet.resolve(objectClazz, id);
		if ( resolved != null ) {
			return resolved;
		}
		// 3. Look up in transaction change set
		return this.transactionChangeSets.resolveOrNull(objectClazz, id);
	}
	
	public boolean isRemoved(Class<? extends DomainObject> objectClazz,Long id ) {
		return this.transactionChangeSets.isRemoved(objectClazz, id);
	}
	
	void addProxy( DomainObject object ) {
		if ( object == null ) {
			throw new NullPointerException( "object" );
		}
		ensureObjectIsNotRegistered( object );
		this.proxyObjectSet.add(object);
		if (this.log.isDebugEnabled()) {
			this.log.debug("Proxy added " + object);
		}
	}
	
	void removeProxy(DomainObject object) {
		this.proxyObjectSet.remove(object);
	}
	
	public void onExternalChanges(DataChangeSet dataChanges, int generation ) {
		EntityConverter entityConvertor = new EntityConverter( generation );
		applyChangeSet(new ChangeSet( dataChanges, entityConvertor, null ) );
		entityConvertor.removeAllInitializedProxies();
	}

	public SelectResult onDataLoaded(Space space, EntitiesSelectResult resultEntities, int generation) {
		if ( space != null ) {
			ensureSpaceExists(space);
		}
		EntityConverter entityConvertor = new EntityConverter( generation );
		final EntityList allEntities = resultEntities.getAllEntities();
		final CrudSet appliedChangeSet = applyChangeSet( new ChangeSet( allEntities, entityConvertor, space ) );
		if (this.log.isDebugEnabled()) {
			this.log.debug( "AppliedChangeSet is " + appliedChangeSet );
		}
		entityConvertor.removeAllInitializedProxies();
		if ( EXTRA_CHECKS ) {
			for( QualifiedObjectId<? extends DomainObject> proxyId : this.proxyObjectSet ) {
				DomainObject cleanObject = this.cleanObjects.resolveOrNull(proxyId); 
				DomainObject proxyObject = this.proxyObjectSet.resolve(proxyId);
				if ( cleanObject != null && proxyObject != null ) {
					throw new IllegalStateException( "Has proxy and non proxy objects. Proxy " + proxyObject + ", Object " + cleanObject );
				}
			}
		}
		// Convert resultEntities to result
		SelectResult result = new SelectResult( resultEntities.getQuery() );
		convertAndAddObjectTo(result.getResultList(), resultEntities.getSelected() );
		result.setResultTotalCount( resultEntities.getSelectedTotalCount() );
		// Apply changes by transaction
		this.transactionChangeSets.applyChangesBy( result.getResultList(), resultEntities.getQuery() );
		// Convert cascaded objects
		convertAndAddObjectTo(result.getCascadedSet(), resultEntities.getCascaded());
		for (FetchedDomainObjectLists objectLists : resultEntities.getFetchedCollections()) {
			DomainObject bean = objectLists.resolveDomainObjectById();
			final BeanMapper<DomainObject> beanMapper = getMapperManager().get( bean );
			for (CascadeFetchedList cascadeFetchedList : objectLists.getCascadeFetchedLists()) {
				final Property<?> property = beanMapper.getProperty( cascadeFetchedList.getPropertyName() );
				List<DomainObject> list = cascadeFetchedList.restoreObjects( bean );
				this.transactionChangeSets.applyChangesBy(list, cascadeFetchedList.getFetchQuery() );
				((ICollectionAccessor)property.getAccessor()).setUpByOrm(bean, list );
			}
		}
		result.setGeneric( resultEntities.getGeneric() );
		result.debugDump( "onDataLoaded result is" );
		return result;		
	}
	
	/**
	 * @param resultChangeSet
	 * @param cascadedSet
	 * @param entity
	 */
	private void convertAndAddObjectTo(Collection<DomainObject> target,
			Iterable<Entity> source) {
		for (Entity entity : source ) {
			QualifiedObjectId<DomainObject> objectId = this.mapper.getObjectId( entity );
			DomainObject domainObject = this.resolveOrNull( objectId );
			if ( domainObject == null ) {
				if ( this.transactionChangeSets.isRemoved( objectId ) ) {					
					continue;
				}
				else {
					throw new ObjectNotFoundException( objectId );
				}
			}
			target.add( domainObject );
		}
	}

	/**
	 * @param objectId
	 * @return
	 */
	private <T extends DomainObject> T resolveOrNull(QualifiedObjectId<T> objectId) {
		if ( objectId == null ) {
			return null;
		}
		else {
			return resolveOrNull( objectId.getObjectClazz(), objectId.getId() );
		}
	}

	private CrudSet applyChangeSet(ChangeSet externalObjects) {
		if (this.transactionChangeSets.size() > 0) {
			ChangeSetMergerContext mergeContext = new ChangeSetMergerContext(
					externalObjects);
			List<TransactionChangeSet> conflicts = new ArrayList<TransactionChangeSet>();
			for (TransactionChangeSet localChangeSet : this.transactionChangeSets) {
				if (!localChangeSet.merge(mergeContext)) {
					conflicts.add(localChangeSet);
					continue;
				}
			}
			if (conflicts.size() > 0) {
				for (TransactionChangeSet conflict : conflicts) {
					conflict.localConflict();
				}
			}
			mergeContext.filterIncomingChangeSet();
			final ChangeSet mergedIncomingChangeSet = mergeContext.getIncomingChangeSet();
			return externalOverwriteAndNotify( mergedIncomingChangeSet );
		} else {
			return externalOverwriteAndNotify( externalObjects );
		}
	}

	private CrudSet externalOverwriteAndNotify( ChangeSet externalObjects ) {
		CleanObjectsOverwriter overwriter = new CleanObjectsOverwriter( externalObjects );
		overwriter.overwrite( this.cleanObjects );
		final CrudSet repositoryChanges = overwriter.getCrudSet();
		repositoryChanges.setOriginalChangeSet( externalObjects );
		// repositoryChanges.setSpace( externalObjects.space );
		addToOrm( repositoryChanges.getCreated() );
		addToOrm( repositoryChanges.getRetrieved() );
		// We don't need to touch updated because it already processed in this.cleanObjects.addCleanOrUpdate
		removeFromOrm( repositoryChanges.getDeleted() );
		return applyChangesToSpacesAndNotify(repositoryChanges);
	}
	
	/**
	 * @param created
	 */
	private void addToOrm(DomainObjectSet objects) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Add to orm " + objects );
		}
		for( DomainObject object : objects ) {
			this.ormManager.add( object );
		}
	}

	Mapper<DomainObject> getMapperManager() {
		return this.mapper;
	}

	TransactionChangeSet beginEdit() {
		TransactionChangeSet changeSet = new TransactionChangeSet(this.domain);
		this.transactionChangeSets.add(changeSet);
		return changeSet;
	}

	void revertTransactionChanges(TransactionChangeSet changeSet) {
		this.domain.getBlobManager().cancelBindedUploads();
		if (this.transactionChangeSets.remove(changeSet)) {
			changeSet.revert(this.cleanObjects);
			if (this.log.isWarnEnabled()) {
				this.log.warn( "Transaction rejected " + changeSet + ". Live transaction count " + this.transactionChangeSets.size() );
			}
		} else {
			this.log.error("Cant' revert changes. Transaction change set not found "
							+ changeSet);
		}
	}

	void applyTransactionChanges(TransactionChangeSet transactionChanges) {
		if (this.transactionChangeSets.remove(transactionChanges)) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Applying changes " + transactionChanges );
			}
			// First remove deleted objects from orm to break related references
			removeFromOrm( transactionChanges.getRemovedObjects() );
			// Apply transaction change set to repository and change object states
			transactionChanges.apply(this.cleanObjects);
			if (this.log.isDebugEnabled()) {
				this.log.debug("Changes " + transactionChanges + " applied. Going to notify." );
			}
			applyChangesToSpacesAndNotify( new CrudSet(transactionChanges) );
		} else {
			this.log.error("Cant' apply changes. Transaction change set not found "
							+ transactionChanges);
		}
		this.domain.getBlobManager().beginBindedUploads();
	}

	/**
	 * @param changeSet
	 * @param changeSet
	 */
	private CrudSet applyChangesToSpacesAndNotify(final CrudSet changeSet) {
		// Froze change set first, see getAlive() method 
		changeSet.froze();
		final Set<DomainObject> undistributedObjects = this.spaces.applyChanges(changeSet);
		if ( !undistributedObjects.isEmpty() ) {
			this.log.warn( "Undistributed objects found " + undistributedObjects );
			evict(undistributedObjects);
			// TODO: [dg] Should we update changeSet? Probably yes, but ...
		}
		try {
			this.listeners.getNotificator().repositoryChanged( changeSet );
		}
		catch( RuntimeException ex ) {
			this.log.error( "Notification failed by " + this.listeners, ex);
		}
		if (this.log.isDebugEnabled()) {
			this.log.debug( this.listeners + " observers notified about " + changeSet + "." );
		}
		return changeSet;
	}

	/**
	 * @param objects
	 */
	private void evict(final Iterable<DomainObject> objects) {
		for( DomainObject object : objects ) {
			evict( object );
		}
	}

	private void evict(DomainObject object) {
		this.cleanObjects.unregistryAndDetach(object);
		this.ormManager.remove(object);
		// Move object to proxy set.
		// It's important because if application has references to this object than it can be reused lately.
		object.ctrl.changeState( ObjectController.State.PROXY );
		this.proxyObjectSet.add( object );
	}
	
	
	/**
	 * @param removedObjects
	 */
	private void removeFromOrm(
			Iterable<DomainObject> removedObjects) {
		for( DomainObject object : removedObjects ) {
			this.ormManager.remove( object );
		}
	}

	boolean unregistryClean(DomainObject domainObject,
			boolean checkObjectIsRegisteredClean) {
		return this.cleanObjects.unregistry(domainObject,
				checkObjectIsRegisteredClean);
	}

	/**
	 * @param domainObject
	 */
	void ensureObjectIsNotRegistered(DomainObject domainObject) {
		DomainObject registered = resolveOrNull(domainObject.getQualifiedId());
		if (registered != null) {
			throw new ObjectRegisteredException(registered, domainObject);
		}
	}

	void ensureObjectIsRegistered(DomainObject domainObject) {
		DomainObject foundObject = resolveOrNull(domainObject.getQualifiedId());
		DomainObjectSet.ensureAreSame(domainObject, foundObject, this);
	}

	public <T extends DomainObject> SynchronizedListProvider<T> getSyncListProvider(TypedQuery<T> fetchCriteria) {
		return this.synchronizedListProviderManager.get(fetchCriteria);
	}
	
	/**
	 * @param from
	 */
	void afterObjectChanged(DomainObject from) {
		this.synchronizedListProviderManager.afterObjectChanged( from );		
	}

	/**
	 * 
	 */
	public void unloadAll() {
		this.log.info( "Unload all from repository ");
		if ( this.transactionChangeSets.size() > 0 ) {
			throw new IllegalStateException( "Can't unload data because transaction changes set is not empty" );
		}
		this.ormManager.clear();
		this.cleanObjects.clear();
		this.proxyObjectSet.clear();
		loadDefaultSpaces();
	}

	/**
	 * 
	 */
	private void loadDefaultSpaces() {
		// TODO change this in future
		ensureSpaceExists( new ConsumeAllSpace() );
	}

	/**
	 * @param space
	 */
	public void ensureSpaceExists(Space space) {
		if ( space == null ) {
			throw new NullPointerException( "space" ); 
		}
		if ( this.spaces.add( space ) ) {
			if (this.log.isDebugEnabled()) {
				this.log.debug( "Space added " + space );
			}			
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug( "Space " + space + " already exists" );
			}
		}
	}
	
	public Set<DomainObject> removeSpace(Space space) {
		Set<DomainObject> releasedObjects = this.spaces.remove(space);
		evict( releasedObjects );
		return releasedObjects;
	}

	/**
	 * @param externalLog
	 */
	public void debugTo(Log externalLog) {
		if ( externalLog.isDebugEnabled() ) {
			externalLog.debug( toDebugString() ); 
		}		
	}

	/**
	 * @return
	 */
	public String toDebugString() {
		ToStringBuilder tsb = new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE );		
		tsb.append( "ormManager", this.ormManager );
		this.cleanObjects.debugTo( tsb );
		return tsb.toString();
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "ormManager", this.ormManager );		
		return tsb.toString();
	}

	/**
	 * @param space
	 * @param item
	 */
	public void addObjectToSpace(Space space, DomainObject item) {
		this.spaces.addObjectToSpace(space, item );
	}

	/**
	 * @param space
	 * @param item
	 */
	public void removeObjectFromSpace(Space space, DomainObject item) {
		this.spaces.removeObjectFromSpace(space, item );
	}
	
	public DomainStatistics getDomainStatistics() {
		return new DomainStatistics( this.transactionChangeSets.size(), this.listeners.size(), this.cleanObjects.size(), this.spaces.size(), this.proxyObjectSet.size() );
	}

	/**
	 *
	 */
	private final class EntityConverter implements IEntityConvertor {
		
		protected final org.apache.commons.logging.Log log = Repository.this.log;
				
		private final int generation;
		
		private final Set<DomainObject> initializedProxies = new HashSet<DomainObject>();
		
		public EntityConverter(int generation) {
			super();
			this.generation = generation;
		}
		
		public DomainObject convert(Entity entity, ObjectController.State initialState) {
			final BeanMapper<DomainObject> beanMapper = Repository.this.mapper.get(entity);
			QualifiedObjectId<? extends DomainObject> objectId = beanMapper.getObjectId(entity);
			DomainObject object;
			if ( Repository.this.cleanObjects.contains(objectId) ||
				 Repository.this.transactionChangeSets.contains( objectId ) ) {
				if ( initialState == ObjectController.State.NEW ) {
					this.log.error( "New object, that already exists in domain " + objectId );
				}
				else if ( initialState != ObjectController.State.DIRTY &&
					 initialState != ObjectController.State.REMOVED ) {
					throw new IllegalArgumentException( "Unexpected initial state " + initialState + " for " + objectId ); 
				}
				if ( beanMapper.isInheritanceBase() ) {
					this.log.error( "Base entity detected " + beanMapper.getObjectClass().getSimpleName() + ""  );					
					// throw new IllegalArgumentException( "Can't create " + beanMapper.getObjectClass().getSimpleName() + " by " + entity );
				}
				// Clean objects already contains this object, so we have overwrite case
				// Creates new unmanaged object that can be used to do overwrite
				// TODO think about overwrite optimization
				object = beanMapper.toObject(entity,true);
				if (this.log.isDebugEnabled()) {
					this.log.debug("Created new object to overwrite " + object );
				}				
			}
			else {
				object = Repository.this.proxyObjectSet.resolve(objectId);
				if ( object == null ) {
					if ( beanMapper.isInheritanceBase() ) {
						this.log.error( "Base entity detected " + beanMapper.getObjectClass().getSimpleName() + ""  );					
						// throw new IllegalArgumentException( "Can't create " + beanMapper.getObjectClass().getSimpleName() + " by " + entity );
					}
					object = DomainObjectInterceptor.newProxy( objectId.getObjectClazz(), objectId.getId() );
					Repository.this.addProxy(object );
				}
				if ( this.initializedProxies.contains( object ) ) {
					throw new IllegalStateException( "Proxy object already used " + object );
				}
				this.initializedProxies.add( object );
				if (this.log.isDebugEnabled()) {
					this.log.debug("Find proxy " + object );
				}
				object.ctrl.changeState( ObjectController.State.DETACHED );
				// Fill proxy object
				beanMapper.setUpManagedFeatures(object);
				beanMapper.toObject(object, entity);
			}
			object.ctrl.changeState(initialState);
			object.ctrl.setGeneration(this.generation);
			return object;
		}
		
		public void removeAllInitializedProxies() {
			ProxyObjectSet proxyObjectSet = Repository.this.proxyObjectSet;  
			for( DomainObject object : this.initializedProxies ) {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Remove proxy " + object );
				}
				proxyObjectSet.remove(object);
			}
		}

	}

	public void normalizePopulation() {
		if ( this.cleanObjects.size() > MAX_POPULATION ){
			this.log.warn( $( "Population {0} is more then {1}", this.cleanObjects.size(), MAX_POPULATION ) );
			try {
				this.unloadAll();
			}
			catch( RuntimeException ex ) {
				this.log.error( "Can't unload objects", ex );
			}
		}
	}
	
	
}
 