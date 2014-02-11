package ss.lab.dm3.persist;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.blob.BlobManager;
import ss.lab.dm3.blob.service.BlobTransferService;
import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.connection.Waiter;
import ss.lab.dm3.connection.WaiterCheckpoint;
import ss.lab.dm3.connection.service.AbstractServiceProvider;
import ss.lab.dm3.events.EventManager;
import ss.lab.dm3.orm.IBeanMapperProvider;
import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.ObjectResolver;
import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.MapperFactory;
import ss.lab.dm3.orm.mapper.MapperParamerts;
import ss.lab.dm3.persist.backend.DataManagerBackEndAdaptor;
import ss.lab.dm3.persist.backend.DataManagerBackEndListener;
import ss.lab.dm3.persist.backend.EntitiesSelectResult;
import ss.lab.dm3.persist.changeset.DataChangeSet;
import ss.lab.dm3.persist.id.SecureRandomIdGenerator;
import ss.lab.dm3.persist.lock.multithread.MultithreadDomainLockStrategy;
import ss.lab.dm3.persist.query.GenericQuery;
import ss.lab.dm3.persist.script.builtin.StartupLoaderScript;
import ss.lab.dm3.persist.service.DataProviderAsync;
import ss.lab.dm3.persist.space.Space;
import ss.lab.dm3.persist.stat.DomainStatistics;
import ss.lab.dm3.security2.SecurityManager;
import static d1.FastAccess.*;

/**
 * @author Dmitry Goncharov
 */
public class Domain {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());

	private final AbstractServiceProvider serviceProvider;
	
	private final Mapper<DomainObject> mapper;

	private Repository repository;

	private DataProviderAsync dataProvider;

	private volatile Thread domainThread = null;

	private Transaction transaction = null;

	private IDomainLockStrategy domainLockStrategy;
	
	private final DomainIdGenerator idGeneratorStrategy;

	private final OrmManager ormManager;
	
	private final AtomicInteger generation = new AtomicInteger();

	private final DomainChangeManager changeManager;

	private final ContextStack contextes = new ContextStack();

	private BlobManager blobManager;

	private SecurityManager securityManager; 
		
	/**
	 * 
	 */
	public Domain(AbstractServiceProvider serviceProvider,
			EventManager eventManager, SecurityManager securityManager, IDomainLockStrategy domainLockStrategy) {
		super();
		this.serviceProvider = serviceProvider;
		this.securityManager = securityManager;
		// First use multithread domain lock strategy
		this.domainLockStrategy = new MultithreadDomainLockStrategy();
		this.domainLockStrategy.install( this );
		lockOrThrow();
		try {
			this.idGeneratorStrategy = new SecureRandomIdGenerator();
			// First get data provider
			this.dataProvider = serviceProvider.getAsyncService(DataProviderAsync.class);
			// Get DomainMapperManagerParamerts and create objectMapperManager
			CallbackResultWaiter mapperManagerParamertsWaiter = new CallbackResultWaiter();
			this.dataProvider.getMapperManagerParamerts(mapperManagerParamertsWaiter);
			MapperParamerts domainMapperManagerParamerts = mapperManagerParamertsWaiter.waitToResult(MapperParamerts .class);
			final MapperFactory mapperFactory = new MapperFactory();
			this.mapper = mapperFactory.create(domainMapperManagerParamerts, new DomainBeanWrapper() );
			// Create orm manager
			this.ormManager = new OrmManager();
			this.ormManager.setBeanMapperProvider( new OrmBeanMapperProvider());
			this.ormManager.setObjectResolver( new DomainObjectResolver());
			// Create repository
			this.repository = new Repository(this, this.mapper);
			// Add data event listener and wait to successful registration
			final DataManagerBackEndListener safeObserver = createDomainSafeProxy(
				new DataManagerBackEndObserver(), DataManagerBackEndListener.class);
			CallbackResultWaiter listenerRegistrationWaiter = new CallbackResultWaiter();
			eventManager.addListener(DataManagerBackEndListener.class, safeObserver, listenerRegistrationWaiter );
			listenerRegistrationWaiter.waitToResult();
			// Loads initial data
			DomainLoader dataLoader = new DomainLoader( new StartupLoaderScript() );
			CallbackResultWaiter initialLoaderWaiter = new CallbackResultWaiter();
			dataLoader.beginLoad( this, initialLoaderWaiter );
			initialLoaderWaiter.waitToResult();
			// Create change manager
			this.changeManager = new DomainChangeManager( this.repository );
			// Create blob manager
			this.blobManager = new BlobManager(serviceProvider.getSyncProxyService( BlobTransferService.class ) );
		}
		finally {
			unlock();
		}
		// Finally: set up specified domain lock strategy
		this.domainLockStrategy.uninstall();
		this.domainLockStrategy = domainLockStrategy;
		this.domainLockStrategy.install( this );
		this.contextes.push( new Context( this ) );
	}

	/**
	 * @param dataChanges
	 */
	public void onExternalChanges(DataChangeSet dataChanges) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("onExternalChanges " + dataChanges ); 
		}
		this.repository.onExternalChanges( dataChanges, nextGeneration() );
	}
	
	/**
	 * @param space 
	 * @param result
	 * @return 
	 */
	SelectResult onDataLoaded(Space space, EntitiesSelectResult result) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("onDataLoaded " + result );
		}
		return this.repository.onDataLoaded(space, result, nextGeneration() );
	}
	
	/**
	 * @return
	 */
	private int nextGeneration() {
		return this.generation.incrementAndGet();
	}

	/**
	 * 
	 */
	synchronized void checkDomain() {
		if (Thread.currentThread() != getDomainThread()) {
			throw new InvalidThreadAccessException(this);
		}
	}

	public Repository getRepository() {
		checkDomain();
		return this.repository;
	}

	/**
	 * Thread safe runnable execute
	 * 
	 * @param runnable
	 */
	public void execute(Runnable runnable) {
		if (runnable == null) {
			throw new NullPointerException("runnable");
		}
		if (isDomainThread()) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Executing from domain " + runnable );
			}
			runnable.run();
		} else {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Foreign executing " + runnable );
			}
			executeFromNotDomainThread(runnable);
			if (this.log.isDebugEnabled()) {
				this.log.debug("Done foreign executing " + runnable );
			}
		}
	}
	
	public <T> T execute( final Callable<T> callable ) {
		final AtomicReference<T> refResult = new AtomicReference<T>();
		final AtomicReference<Exception> refEx = new AtomicReference<Exception>();
		execute( new Runnable() {
			public void run() {
				try {
					refResult.set( callable.call() );
				} catch (Exception ex) {
					refEx.set( ex );
				}
			}
		} );
		Exception callEx = refEx.get();
		if ( callEx != null ) {
			if ( callEx instanceof RuntimeException ) {
				throw (RuntimeException)callEx;
			}
			else {
				throw new DomainException( "Callable failed " + callable, callEx );
			}
		}
		return refResult.get();			
	}

	/**
	 * @param runnable
	 */
	private void executeFromNotDomainThread(Runnable runnable) {
		this.domainLockStrategy.executeFromNotDomainThread(runnable);
	}

	/**
	 * @return
	 */
	private boolean isDomainThread() {
		return Thread.currentThread() == getDomainThread();
	}

	public <T extends DomainObject> T resolve(Class<T> entityClass, Long id) {
		return resolve(entityClass, id, true );
	}
	
	private <T extends DomainObject> T resolve(Class<T> entityClass, Long id, boolean createProxy ) {
		checkDomain();
		if ( id == null ) {
			return null;
		}
		T object = this.repository.resolveOrNull(entityClass, id);
		if ( object != null ) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Resolved localy " + object + " by " + entityClass.getSimpleName() + "#" + id);
			}
			return object;
		}
		if ( this.repository.isRemoved(entityClass, id) ) {
			return null;
		}
		if ( !createProxy ) {
			return null;
		}
		// 3. Create & returns proxy or go to the server for real object
		BeanMapper<DomainObject> beanMapper = getMapper().get( entityClass );
		if ( beanMapper.isInheritanceBase() ) {
			// Need to go to server because entityClass is base class.
			// So we need to know real class subclass.
			final Context context = getContext();
			LazyObjectLoader objectLoader = context.getLazyObjectLoader();
			return objectLoader.find(context, entityClass, id );
		}
		else {
			// Create & returns proxy
			final T proxy = DomainObjectInterceptor.newProxy(entityClass, id );
			if (this.log.isDebugEnabled()) {
				this.log.debug( "Create proxy " + proxy );
			}
			this.repository.addProxy( proxy );
			return proxy;
		}
	}

	/**
	 * Find will be executed on the server in all cases.
	 */
	public <T extends DomainObject> T find(QualifiedObjectId<T> qualifiedId) {
		return qualifiedId != null ? find( qualifiedId.getObjectClazz(), qualifiedId.getId() ) : null;
	}
	
	/**
	 * Find will be executed on the server in all cases.
	 */
	public <T extends DomainObject> T find(Class<T> entityClazz, Long id ) {
		if ( id == null ) {
			return null;
		}
		final TypedQuery<T> query = QueryHelper.eq(entityClazz, id );
		query.setLimitSize( 1 );
		return this.find( query ).getFirstOrNull();
	}
			
	/**
	 * Find will be executed on the server in all cases.
	 */
	public <T extends DomainObject> DomainObjectCollector<T> find(
			TypedQuery<T> query) {
		checkDomain();
		//TODO think about optimisation. Problem with it is:
		// * not implemented order
		// 
//		if ( query.getLimitSize() == 1 && query.getRestriction() != null && query.getRestriction().isEvaluable() ) {
//			final DomainObjectCollector<T> result = this.repository.find(query);
//			if ( result.size() == 1 ) {
//				return result;
//			}
//		}
		
		
		final Context context = getContext();
		LazyObjectLoader objectLoader = context.getLazyObjectLoader();
		final DomainObjectCollector<T> serverFindResult = objectLoader.find(context, query);
		return serverFindResult;
	}
	
	public <T> T evaluate( GenericQuery<T> query ) {
		checkDomain();
		final Context context = getContext();
		LazyObjectLoader objectLoader = context.getLazyObjectLoader();
		return objectLoader.evaluate(context, query);
	}
	
	public <T> T evaluate( Class<T> clazz, String sql, Object ... params ) {
		return evaluate( QueryHelper.genericSql(clazz, sql, params) );
	}
	
	public <T extends DomainObject> T resolve(QualifiedObjectId<T> id) {
		return resolve( id.getObjectClazz(), id.getId() ); 
	}
	
	/**
	 * @param createEqByLogin
	 * @return
	 */
	public <T extends DomainObject> T resolve(TypedQuery<T> query)  {
		query.setLimitSize( 1 );
		if ( QueryHelper.isIdQuery( query ) ) {
			return resolve( query.getEntityClass(), QueryHelper.getIdQueryValue(query) );
		}
		else {
			return find( query ).getSingleOrNull();
		}
	}
	
	public Transaction beginTrasaction( boolean requireNew ) {
		checkDomain();
		if ( requireNew ) {
			return beginNewTransaction();
		}
		else {
			if ( isInTransaction() ) {
				return new TransactionProxy( this, this.transaction ); 
			}
			else {
				return beginNewTransaction();
			}
		}
	}
	
	/**
	 * 
	 */
	public void commitTrasaction() {
		final Transaction tx = getTransaction();
		tx.commit();
	}

	/**
	 * @return
	 */
	private Transaction beginNewTransaction() {
		handlePreviousTransaction();
		this.transaction = new TransactionImpl(this, nextGeneration());		
		return this.transaction;
	}
	
	public Transaction beginTrasaction() {
		return beginTrasaction( false );
	}

	/**
	 * By default it commits previous transaction
	 */
	private void handlePreviousTransaction() {
		if (this.transaction == null) {
			return;
		}
		if (!this.transaction.isEditable()) {
			this.transaction = null;
			this.log.info("Previous transaction is not editable, so we think that it is finished. "
					+ this.transaction);			
		} else {
			// Problematic situation. 
			// If we don't close previous transaction then we will block all others.
			// If we close it, then we hide the error.
			// So: we rollback existed and throw exception.
			this.log.error("Previous transaction is not finished "
					+ this.transaction + ". Going to rollback it." );
			final String message = "Transaction already exists " + this.transaction  
					+ ". It was rollbacked." 
					+ " Please check the trasaction management code.";
			try {
				this.transaction.rollback();
			}
			catch( RuntimeException ex ) {
				this.log.error("Can't rollback transaction " + this.transaction );
			}
			this.transaction = null;
			throw new DomainException( message );
		}
	}

	public void dispose() {
		if ( isDisposed() ) {
			return;
		}
		if (!isLocked()) {
			log.warn("Trying to dispose locked domain " + this );
			synchronized(this) {
				if ( !isLocked() ) {
					try {
						tryLock( MultithreadDomainLockStrategy.DEFAULT_TIME_OUT );
					}
					catch( RuntimeException ex ) {
						log.warn("Failed to lock domain " + this );
						throw new DomainException( $("Can't dispose domain {0}", this), ex );
					}
				}
			}
		}
		// Domain is locked
		this.serviceProvider.dispose();
	}

	public synchronized Thread getDomainThread() {
		return this.domainThread;
	}

	public synchronized void lockOrThrow() throws IllegalStateException {
		if (isLocked()) {
			throw new IllegalStateException("Can't lock non free domain " + this );
		}
		this.domainThread = Thread.currentThread();
		DomainThreadsManager.INSTANCE.bind(this);
	}

	public synchronized boolean tryLock(long timeout) {
		long startWaitTime = System.currentTimeMillis();
		while (isLocked()) {
			try {
				this.wait(timeout);
			} catch (InterruptedException ex) {
			}
			if (System.currentTimeMillis() - startWaitTime > timeout) {
				return false;
			}
		}
		lockOrThrow();
		return true;
	}

	public synchronized boolean isLocked() {
		return this.domainThread != null;
	}
	
	public synchronized boolean isDisposed() {
		return this.serviceProvider.isDisposed();
	}

	public synchronized void unlock() {
		if (this.domainThread == Thread.currentThread()) {
			this.domainThread = null;
			DomainThreadsManager.INSTANCE.unbind(this);
		} else {
			throw new InvalidThreadAccessException(this);
		}
		this.notifyAll();
	}

	/**
	 * @return the current transaction or null if no transaction found
	 */
	public Transaction getTransaction() {
		checkDomain();
		if (isInTransaction()) {
			return this.transaction;
		} else {
			return null;
		}
	}

	public boolean isInTransaction() {
		checkDomain();
		return this.transaction != null && this.transaction.isEditable();
	}

	public ICallbackHandler createDomainSafeProxy(ICallbackHandler handler) {
		return createDomainSafeProxy(handler, ICallbackHandler.class);
	}

	public <T> T createDomainSafeProxy(T impl, Class<T> interfaze) {
		return DomainSafeProxy.create(this, impl, interfaze);
	}

	/**
	 * 
	 */
	private void checkInInTransaction() {
		if (!isInTransaction()) {
			throw new ObjectNotInTransactionException(this);
		}
	}

	/**
	 * @param objClazz
	 * @return
	 */
	Long createId(DomainObject object) {
		checkDomain();
		checkInInTransaction();
		return this.idGeneratorStrategy.createId(object);
	}

	/**
	 * @param domainObject
	 */
	void registryDirty(DomainObject domainObject) {
		checkDomain();
		checkInInTransaction();
		TransactionImpl.getTransactionImpl( this.transaction ).registryDirty(domainObject);
	}
	
	

	/**
	 * @param domainObject
	 */
	void registryNew(DomainObject domainObject) {
		checkDomain();
		checkInInTransaction();
		if (this.log.isDebugEnabled()) {
			this.log.debug("Registry new " + domainObject );
		}
		TransactionImpl.getTransactionImpl( this.transaction ).registryNew(domainObject);
	}

	/**
	 * @param domainObject
	 */
	void registryRemoved(DomainObject domainObject) {
		checkDomain();
		checkInInTransaction();
		TransactionImpl.getTransactionImpl( this.transaction ).registryRemoved(domainObject);
	}

	/**
	 * @return
	 */
	public Mapper<DomainObject> getMapper() {
		checkDomain();
		return this.mapper;
	}

	/**
	 * 
	 */
	public DataProviderAsync getDataProvider() {
		return this.dataProvider;
	}

	/**
	 * 
	 */

	/**
	 * @return
	 */
	public static Waiter createResponseWaiter() {
		Domain currentDomain = DomainResolverHelper.getCurrentDomainOrNull();
		if (currentDomain != null) {
			return currentDomain.domainLockStrategy.createWaiter();
		} else {
			return new Waiter();
		}		
	}
	
	public static void sleep(long mils) {
		Waiter waiter = Domain.createResponseWaiter();
		WaiterCheckpoint checkpoint = new WaiterCheckpoint();
		synchronized (checkpoint) {
			waiter.await( checkpoint, mils );
		}
	}
	
	/**
	 * @return
	 */
	public OrmManager getOrmManager() {
		return this.ormManager;
	}

	/**
	 * @return
	 */
	public int getLastGeneration() {
		return this.generation.get();
	}
	

	public void addListener(DomainChangeListener listener, Class<? extends DomainObject> objectClass) {
		this.changeManager.addListener(listener, objectClass);
	}

	public void addListener(DomainChangeListener listener,
			Class<? extends DomainObject>[] involvedClasses) {
		this.changeManager.addListener(listener, involvedClasses);
	}

	public void removeListener(DomainChangeListener listener,
			Class<? extends DomainObject> objectClass) {
		this.changeManager.removeListener(listener, objectClass);
	}

	public void removeListener(DomainChangeListener listener) {
		this.changeManager.removeListener(listener);
	}

	/**
	 * @param context
	 * @param runnable
	 */
	public void execute(Context context, Runnable runnable) {
		if ( context == null ) {
			context = getContext();			
		}
		beginContext(context);
		try {
			execute( runnable );
		}
		finally {
			releaseContext(context);
		}		
	}

	/**
	 * @param context
	 * @param runnable
	 */
	public <T> T execute(Context context, Callable<T> callable) {
		if ( context == null ) {
			context = getContext();			
		}
		beginContext(context);
		try {
			return execute( callable );
		}
		finally {
			releaseContext( context );
		}		
	}

	/**
	 * 
	 */
	public void releaseContext(Context context) {
		this.contextes.pop( context );
	}

	/**
	 * @param context
	 */
	public void beginContext(Context context) {
		if ( context == null ) {
			throw new NullPointerException( "context" );
		}
		this.contextes.push( context );
	}
	
	
	
	public Context getContext() {
		checkDomain();
		final Context current = this.contextes.getCurrent();
		if ( current == null ) {
			throw new IllegalStateException( "Current context is null " + this.contextes );
		}
		return current;
	}

	/**
	 * @param context
	 */
	void release(Context context) {
		checkDomain();
		if ( isContextInUse(context) ) {
			throw new IllegalArgumentException( "Can't release context that is in the context stack" );
		}
		this.repository.removeSpace( context.getSpace() );
	}

	/**
	 * @param context
	 * @return
	 */
	public boolean isContextInUse(Context context) {
		return this.contextes.contains( context );
	}
	
	/**
	 * @param baseProxyObject
	 */
	void loadDataForProxy(DomainObject object) {
		if ( object == null ) {
			throw new NullPointerException( "object" );
		}
		Context context = getContext();
		try {
			final DomainObject loadedObject = context.getLazyObjectLoader().find( context, object.ctrl.getEntityClass(), object.getId() );
			if ( loadedObject != object ) {
				throw new IllegalStateException( "Invalid object loaded " + loadedObject + " by " + object );
			}
			if ( !loadedObject.ctrl.isClean() ) {
				throw new IllegalStateException( "Object is not clean " + loadedObject );
			}
		}
		catch( ObjectNotFoundException ex ) {
			object.ctrl.changeState( ObjectController.State.DETACHED );
			this.repository.removeProxy( object );
			throw ex;
		}
	}

	public <T extends DomainObject> T createObject(Class<T> beanClazz) {
		return createObject(beanClazz, null );
	}
	
	public <T extends DomainObject> T createObject(Class<T> beanClazz, Long id ) {
		checkDomain();
		T object = beanClazz.cast( this.mapper.get( beanClazz ).createObject( true ) );
		if ( id != null ) {
			object.ctrl.markNew( id );
		}
		else {
			object.ctrl.markNew();
		}
		return object;
	}

	/**
	 *
	 */
	private final class OrmBeanMapperProvider implements IBeanMapperProvider { 
		
		public BeanMapper<?> get(MappedObject bean) {
			return getMapper().get((DomainObject)bean );
		}

		public BeanMapper<?> get(String qualifier) {
			return getMapper().get(qualifier);
		}
		
		@SuppressWarnings("unchecked")
		public BeanMapper<?> get(Class<? extends MappedObject> beanClazz) {
			final Class<? extends DomainObject> domainClazz = (Class<? extends DomainObject>) beanClazz;
			return getMapper().get( domainClazz );
		}

		public Mapper<?> get() {
			return getMapper();
		}
		
		
	}
	
	/**
	 *
	 */
	private final class DomainObjectResolver extends ObjectResolver {
		
		@SuppressWarnings("unchecked")
		@Override
		public <T extends MappedObject> T resolveManagedOrNull(Class<T> entityClass, Long id) {
			final DomainObject resolved = Domain.this.resolve((Class<? extends DomainObject>)entityClass, id, false );
			if ( resolved != null && isObjectManagedByOrm(resolved) ) {
				return entityClass.cast(resolved);
			}
			else {
				return null;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T extends MappedObject> T resolve(Class<T> entityClass,
				Long id) {
			final DomainObject resolved = Domain.this.resolve((Class<? extends DomainObject>)entityClass, id);
			return entityClass.cast(resolved);
		}
		
		@Override
		public boolean isObjectManagedByOrm(MappedObject object) {
			final ObjectController ctrl = ((DomainObject)object).ctrl;
			return !ctrl.isDetached() && !ctrl.isProxy();
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
	private final class DataManagerBackEndObserver extends
	DataManagerBackEndAdaptor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.lab.dm3.persist.backend.DataManagerBackEndAdaptor#dataCommitted(ss.lab.dm3.persist.changeset.DataChangeSet)
		 */
		@Override
		public void dataCommitted(DataChangeSet dataChanges) {
			super.dataCommitted(dataChanges);
			if (log.isDebugEnabled()) {
				log.debug("Data commited to " + Domain.this + ". Change set " + dataChanges  );
			}
			onExternalChanges(dataChanges);
		}
	}

	/**
	 * @return
	 */
	public DomainStatistics getStatistics() {
		checkDomain();
		return this.repository.getDomainStatistics();
	}

	/**
	 * @return
	 */
	public BlobManager getBlobManager() {
		return this.blobManager;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append( "alive", !isDisposed() );
		final Thread domainThread = getDomainThread();
		tsb.append( "domainThread", domainThread != null ? domainThread.getName() : null );
		return tsb.toString();
	}

	public SecurityManager getSecurityManager() {
		return this.securityManager;
	}


	
	
}
