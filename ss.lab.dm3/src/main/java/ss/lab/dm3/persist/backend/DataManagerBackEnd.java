/**
 * 
 */
package ss.lab.dm3.persist.backend;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;

import ss.lab.dm3.blob.backend.BlobInformationProvider;
import ss.lab.dm3.connection.configuration.Configuration;
import ss.lab.dm3.events.Category;
import ss.lab.dm3.events.backend.IEventManagerBackEnd;
import ss.lab.dm3.orm.IBeanMapperProvider;
import ss.lab.dm3.orm.IObjectResolver;
import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.ObjectResolver;
import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.OrmManagerResolveHelper;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.BeanWrapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.MapperFactory;
import ss.lab.dm3.orm.mapper.MapperParamerts;
import ss.lab.dm3.persist.DomainException;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.FetchedDomainObjectLists;
import ss.lab.dm3.persist.Query;
import ss.lab.dm3.persist.backend.hibernate.HibernateUtils;
import ss.lab.dm3.persist.backend.hibernate.ObjectSelector;
import ss.lab.dm3.persist.backend.hibernate.QueryConverter;
import ss.lab.dm3.persist.backend.hibernate.QuerySelectResult;
import ss.lab.dm3.persist.backend.hibernate.SessionManager;
import ss.lab.dm3.persist.backend.hibernate.SessionManagerOpenSession;
import ss.lab.dm3.persist.backend.script.ReadScriptContext;
import ss.lab.dm3.persist.backend.script.ReadScriptHandler;
import ss.lab.dm3.persist.backend.script.ScriptManager;
import ss.lab.dm3.persist.backend.search.SearchEngine;
import ss.lab.dm3.persist.backend.search.SearchIndexEditor;
import ss.lab.dm3.persist.changeset.ChangeSet;
import ss.lab.dm3.persist.changeset.DataChangeSet;
import ss.lab.dm3.persist.changeset.EditableChangeSet;
import ss.lab.dm3.persist.query.GenericQuery;
import ss.lab.dm3.persist.query.SqlExpression;
import ss.lab.dm3.persist.script.QueryScript;
import ss.lab.dm3.persist.script.builtin.StartupLoaderScript;
import ss.lab.dm3.persist.search.ISearchable;

/**
 * 
 * TODO review "session.load" usage. It can cause problems with inheritance
 *   
 * @author Dmitry Goncharov
 */
public class DataManagerBackEnd implements IDataManagerBackEnd {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());

	private final Mapper<DomainObject> mapper;

	private final MapperParamerts domainMapperParamerts;

	private final IEventManagerBackEnd eventManager;

	private final OrmManager dataOrmManager = new OrmManager();

	private final SessionManager sessionManager;

	private final ScriptManager scriptManager = new ScriptManager();

	private final SearchEngine searchEngine;

	private final BlobInformationProvider blobInformationProvider;

	private final ReadWriterLockProvider lockProvider;

	public DataManagerBackEnd( Configuration configuration, IEventManagerBackEnd eventManager ) {
		/**
		 * TODO use single session factory
		 */
		this.sessionManager = new SessionManagerOpenSession( HibernateUtils.createSessionFactory( configuration ) ); 
		this.eventManager = eventManager;
		this.eventManager.addEventFilter(Category.create( DataManagerBackEndListener.class ), FilteredDataManagerBackEndListener.class );
		final MapperFactory mapperFactory = new MapperFactory();
		this.mapper = mapperFactory.create( DomainObject.class, configuration.getDomainDataClasses().toArray(), new BeanWrapper<DomainObject>() {
			@Override
			public Class<? extends DomainObject> getBeanClass(DomainObject objectClazz) {
				return objectClazz.getEntityClass();
			}
		});
		this.domainMapperParamerts = new MapperParamerts( this.mapper.getBeanSpace() );
		this.dataOrmManager.setBeanMapperProvider( new DataObjectMapperProvider());
		this.dataOrmManager.setObjectResolver( new DataObjectResolver());
		this.scriptManager.add( configuration.getScriptHandlers() );
		this.searchEngine = new SearchEngine( configuration.getSearchConfiguration() );
		this.blobInformationProvider = new PersistBlobInformationProvider( this.sessionManager );
		this.lockProvider = configuration.getLockProvider(); 
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.persist.core.ICoreDataManager#selectData(ss.lab.dm3.persist.Criteria)
	 */
	public EntitiesSelectResult selectData(Query query) {
		this.lockProvider.readLock();
		try {
			OrmManagerResolveHelper.beginInterceptionForCurrentThread( this.dataOrmManager );
			final Session session = this.sessionManager.begin();
			try {
				if (this.log.isDebugEnabled()) {
					this.log.debug("selecting data by " + query );
				}
				EntitiesSelectResult selectResult = new EntitiesSelectResult( query );
				if ( query instanceof GenericQuery ) {
					genericQuery(selectResult, new QueryConverter( session, this.mapper ) );				
				} 
				else {
					entityQuery(selectResult, session);				
				}
				this.sessionManager.commit();
	//			// Fix problem with wrong object classes
	//			if ( baseObjects.size() > 0 ) {
	//				addBaseObjects(selectResult, baseObjects);
	//			}
				selectResult.debugDump( "Select result is");
				return selectResult;
			}
			catch( RuntimeException ex ) {
				this.sessionManager.rollback( false );
				throw new DomainException( "Can't perform select by " + query, ex);
			}
			finally {
				this.sessionManager.end();
				OrmManagerResolveHelper.endInterceptionForCurrentThread();
			}
		}
		finally {
			this.lockProvider.readUnlock();
		}
	}

	/**
	 */
	private void genericQuery(EntitiesSelectResult selectResult, QueryConverter queryConverter) {
		final GenericQuery<?> genericQuery = (GenericQuery<?>) selectResult.getQuery();
		final SqlExpression sqlRestriction = genericQuery.getRestriction();
		final SQLQuery sqlQuery = queryConverter.createSqlQuery(sqlRestriction);
		List<?> result = sqlQuery.list();
		if ( result != null && result.size() > 0 ) {
			if ( genericQuery.getGenericClazz().isArray() ) {
				final Class<?> targetItemType = genericQuery.getGenericClazz().getComponentType();
				Object[] items = (Object[]) Array.newInstance( targetItemType, result.size() );
				for( int n = 0; n < items.length; ++ n ) {
					final Object rawItem = result.get( n );
					items[ n ] = convertTo( targetItemType, rawItem );
				}
				selectResult.setGeneric( items );
			}
			else {
				if ( result.size() > 1 ) {
					throw new DomainException( "Selected more than one rows (" + result.size() + " by " + genericQuery );
				}
				selectResult.setGeneric( result.get(0) );
			}	
		}
	}

	/**
	 * @param targetItemType
	 * @param rawItem
	 * @return
	 */
	private Object convertTo(Class<?> targetItemType, Object rawItem) {
		if ( rawItem == null ) {
			return null;	
		}
		else if ( targetItemType.isInstance( rawItem ) ) {
			return rawItem;
		}
		else if ( rawItem instanceof Number ) {
			Number rawNumber = (Number) rawItem;
			if ( targetItemType == Long.class ) {
				return rawNumber.longValue();
			}
			else {
				throw new IllegalArgumentException( "Unsuported target type " + targetItemType + ". Can't convert " + rawNumber );
			}
		}
		else {
			return targetItemType.cast( rawItem );
		}
	}

	private void entityQuery(EntitiesSelectResult selectResult, final Session session) {
		final ObjectSelector objectSelector = new ObjectSelector( session, this.mapper, this.searchEngine );
		final ObjectCollector collector = collect( selectResult.getQuery(), objectSelector);
		if (this.log.isDebugEnabled()) {
			this.log.debug("selected " + collector );
		} 
		DomainObjectConverter converter = new DomainObjectConverter(session, this.mapper);
		selectResult.setSelectedTotalCount( collector.getSelectedTotalCount() );
		for( DomainObject object : collector.getSelected() ) {
			Entity entity = converter.toEntity(object);
			selectResult.getSelected().add(entity );
		}	
		for( DomainObject object : collector.getCascsded() ) {
			Entity entity = converter.toEntity(object);
			selectResult.getCascaded().add(entity );
		}
		for ( FetchedDomainObjectLists objectLists : collector.getFetchedLists()) {
			selectResult.getFetchedCollections().add( objectLists );
		}
	}

	

	/**
	 * @param query
	 * @param objectSelector
	 * @return
	 */
	private ObjectCollector collect(Query query,
			final ObjectSelector objectSelector) {
		if ( query instanceof QueryScript ) {
			QueryScript script = (QueryScript) query;
			if ( script.getClass() == StartupLoaderScript.class &&
				 this.scriptManager.findHandler( script ) == null ) {
				return new ObjectCollector(objectSelector);
			}
			else {
				ReadScriptContext<QueryScript> scriptContext = new ReadScriptContext<QueryScript>( script, objectSelector );
				ReadScriptHandler<QueryScript> handler = this.scriptManager.getHandler( script );
				handler.handle( scriptContext );
				return scriptContext.getCollector();
			}
		}
		else {
			ObjectCollector collector = new ObjectCollector(objectSelector);
			QuerySelectResult selectedObjecs = objectSelector.select( query );
			collector.add( selectedObjecs );
			collector.setSelectedTotalCount( selectedObjecs.getItemsTotalCount() );
			return collector;
		}
	}
	
	private ChangeSet applyChanges( Session session, DataChangeSet dataChanges ) {
		EditableChangeSet changeSet = new EditableChangeSet( dataChanges.getId() );
		final IObjectResolver originalObjectResolver = this.dataOrmManager.getObjectResolver();
		try {
			final CreatedObjectResolver createdObjectResolver = new CreatedObjectResolver( originalObjectResolver, this.mapper, dataChanges.getCreated() );
			this.dataOrmManager.setObjectResolver( createdObjectResolver );
			createdObjectResolver.loadCreatedObjectDataFromEntities();
			for (DomainObject createdObject : createdObjectResolver.getCreatedObjects() ) {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Created " + createdObject);
				}
				session.save(createdObject);
				changeSet.addNew(createdObject);
			}
			for (Entity updatedEntity : dataChanges.getUpdated()) {
				QualifiedObjectId<DomainObject> dataObjectId = this.mapper.getObjectId( updatedEntity );
				final Class<DomainObject> dataClazz = dataObjectId.getObjectClazz();
				DomainObject updatedObject = dataClazz.cast( session.load( dataClazz, dataObjectId.getId() ) );
				// TODO check actualEntity version
				// -------------------------------					
				// Gets bean mapper from updatedEntityId, 
				// because hibernate can return proxy object instead of real data object
				this.mapper.get( dataClazz ).load(updatedObject, updatedEntity.getValues());
				if (this.log.isDebugEnabled()) {
					this.log.debug("Updated " + updatedEntity);
				}
				changeSet.addDirty(updatedObject);
			}				
			for (Entity deletedDto : dataChanges.getDeleted()) {
				QualifiedObjectId<? extends DomainObject> objectId = this.mapper.getObjectId(deletedDto);
				DomainObject deletedObject = (DomainObject) session.load( objectId.getObjectClazz(), objectId.getId() );					
				if (this.log.isDebugEnabled()) {
					this.log.debug("Deleting " + deletedObject);
				}
				this.dataOrmManager.remove(deletedObject);					
				session.delete(deletedObject);
				changeSet.addRemoved(deletedObject);
			}
		}
		finally {
			this.dataOrmManager.setObjectResolver( originalObjectResolver );
		}
		return changeSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.persist.core.ICoreDataManager#commitData(ss.lab.dm3.persist.DataChangesSet)
	 */
	public void commitData(DataChangeSet dataChanges) {
		this.lockProvider.writeLock();
		try {
			OrmManagerResolveHelper.beginInterceptionForCurrentThread( this.dataOrmManager );
			SearchIndexEditor searchIndexEditor = null;
			Session session = this.sessionManager.begin();
			try {
				// Open session, transaction, search index editor 
				searchIndexEditor = this.searchEngine.beginEdit();
				// Apply changes to database
				ChangeSet changes = applyChanges(session, dataChanges);
				// Apply changes to search engine
				try {
					// TODO fix locking problem
					searchIndexEditor.applyChanges(changes);
				}
				catch( RuntimeException ex ) {
					// Can't update search index. 
					// Log error but don't prevent data commit.
					this.log.error( "Can't update search index by " + changes, ex );
				}
				this.sessionManager.commit();
			}
			catch( RuntimeException ex ) {
				this.sessionManager.rollback( false );
				throw new DomainException( "Can't commit change set " + dataChanges, ex );
			}
			finally {
				this.sessionManager.end();
	//			if ( searchIndexEditor != null && searchIndexEditor.isEditable() ) {
	//				searchIndexEditor.rollback();
	//			}
				OrmManagerResolveHelper.endInterceptionForCurrentThread();
			}
		}
		finally {
			this.lockProvider.writeUnlock();			
		}
		// Notify about data change listener
		final DataManagerBackEndListener listener = this.eventManager.getEventNotificator(DataManagerBackEndListener.class);
		listener.dataCommitted(dataChanges);
	}
	
	public synchronized void searchReindex() {
		OrmManagerResolveHelper.beginInterceptionForCurrentThread( this.dataOrmManager );
		this.sessionManager.begin();
		try {
			final List<Class<? extends DomainObject>> knownSubclasses = this.mapper.getKnownSubclasses(DomainObject.class);
			final ObjectSelector objectSelector = new ObjectSelector( SessionManager.getCurrentSession(), this.mapper, this.searchEngine );
			SearchIndexEditor searchIndexEditor = this.searchEngine.beginEdit();
			List<DomainObject> toIndexSet = new ArrayList<DomainObject>();
			for (Class<? extends DomainObject> classDomainObject : knownSubclasses) {
				if ( ISearchable.class.isAssignableFrom( classDomainObject ) ) {
					if ( this.log.isDebugEnabled() ) {
						this.log.debug( "Implements ISearchable : " + classDomainObject.getSimpleName() );
					}
					final List<? extends DomainObject> select = objectSelector.select( classDomainObject );
					for (DomainObject domainObject : select) {
						if ( this.log.isDebugEnabled() ) {
							this.log.debug( domainObject );
						}
					}
					toIndexSet.addAll( select );
				}
			}			
			searchIndexEditor.reIndex( toIndexSet );
			this.sessionManager.commit();
		}
		catch( RuntimeException ex ) {
			this.sessionManager.rollback( false );
			throw new DomainException( "Can't reindex", ex );
		}
		finally {
			this.sessionManager.end();
			OrmManagerResolveHelper.endInterceptionForCurrentThread();
		}
		
	}

	public Mapper<DomainObject> getMapper() {
		return this.mapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.persist.backend.IDataManagerBackEnd#getDomainMapperManagerParamerts()
	 */
	public MapperParamerts getMapperParamerts() {
		return this.domainMapperParamerts;
	}
	
	/**
	 * 
	 */
	private final class DataObjectResolver extends ObjectResolver {
		@Override
		public <T extends MappedObject> T resolve(Class<T> entityClass,
				Long id) {
			if ( id == null ) {
				return null;
			}
			if ( !DomainObject.class.isAssignableFrom( entityClass ) ) {
				throw new IllegalArgumentException( "Unexpected entity class " + entityClass );
			}
			Session session = SessionManager.getCurrentSession();
			// TODO think about variant of loading, 
			/// It's seems that we load more than required, because we 
			// should not update not loaded entities/collections
			return entityClass.cast( session.load( entityClass, id ) );
		}

		@Override
		public boolean isObjectManagedByOrm(MappedObject object) {
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends MappedObject> QualifiedObjectId<? extends T> getQualifiedObjectId(
				T bean) {
			return bean != null ? (QualifiedObjectId<? extends T>) ((DomainObject) bean).getQualifiedId() : null;
		}
		
	}

	/**
	 *
	 */
	private final class DataObjectMapperProvider implements IBeanMapperProvider {
		public BeanMapper<?> get(MappedObject bean) {
			DomainObject dataObject = (DomainObject) bean;
			return DataManagerBackEnd.this.mapper.get( dataObject.getEntityClass() );
		}

		public BeanMapper<?> get(String qualifier) {
			return DataManagerBackEnd.this.mapper.get( qualifier );
		}

		@SuppressWarnings("unchecked")
		public BeanMapper<?> get(Class<? extends MappedObject> beanClazz) {
			final Class<? extends DomainObject> domainClazz = (Class<? extends DomainObject>) beanClazz;
			return DataManagerBackEnd.this.mapper.get( domainClazz );
		}

		public Mapper<?> get() {
			return DataManagerBackEnd.this.mapper;
		}
		
	}

	/**
	 * @return
	 */
	public BlobInformationProvider getBlobInformationProvider() {
		return this.blobInformationProvider;
	}

}
