package ss.framework.domainmodel2;

import java.util.Hashtable;

import ss.common.ArgumentNullPointerException;
import ss.common.IdentityUtils;
import ss.common.ReflectionUtils;
import ss.framework.domainmodel2.DomainSpaceObjects.CommitFailedException;

public abstract class AbstractDomainSpace {

	@SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractDomainSpace.class);

	private final String id = IdentityUtils.generateUuid().toString();
	
	private final DomainSpaceObjects objects = new DomainSpaceObjects(this);

	private final AliveDomainObjectCollectionList aliveCollections = new AliveDomainObjectCollectionList();

	private Hashtable<Class, AbstractHelper> helperClassToHelperInstance = new Hashtable<Class, AbstractHelper>();

	private volatile Transaction transaction = null;
	
	private final Object transactionChangeMutex = new Object();
	
	private volatile IDataProvider dataProvider = NullDataProvider.INSTANCE; 
	
	private final Object dataProviderMutex = new Object();
	
	private final TempIdGenerator tempIdGenerator = new TempIdGenerator();
	
	protected AbstractDomainSpace() {
	}

	/**
	 * @return
	 */
	protected final IDataProvider checkAndGetDataProvider() {
		synchronized(this.dataProviderMutex)  {
			if ( !this.dataProvider.isAlive() ) {
				reconnectDataProvider();
			}
			return this.dataProvider;
		}
	}
	
	/**
	 * 
	 */
	protected abstract void reconnectDataProvider();
	
	public final void resetDataProvider( IDataProvider caller ) {
		synchronized(this.dataProviderMutex)  {
			if ( this.dataProvider == caller ) {
				setDataProvider( NullDataProvider.INSTANCE );
			}
		}
	}
	
	/**
	 * @param dataProvider the dataProvider to set
	 */
	public void setDataProvider(IDataProvider dataProvider) {
		synchronized(this.dataProviderMutex)  {
			if ( this.dataProvider == dataProvider ) {
				return;
			}
			if ( dataProvider == null ) {
				throw new ArgumentNullPointerException( "dataProvider" );
			}
			beforeChangeDataProvider( this.dataProvider );
			this.dataProvider = dataProvider;
			this.dataProvider.addDataProviderListener(this.dataProviderListener);
		}
	}

	/**
	 * 
	 */
	protected void beforeChangeDataProvider( IDataProvider oldDataProvider ) {
		oldDataProvider.removeDataProviderListener(this.dataProviderListener);		
	}

	/**
	 * @return
	 */
	private AliveDomainObjectCollectionList getAliveCollections() {
		return this.aliveCollections;
	}

	/**
	 * @param object
	 */
	private void notifyObjectNew(DomainObject object) {
		final AliveDomainObjectCollectionList controlledCollections = getAliveCollections();
		for (DomainObjectCollection<DomainObject> collection : controlledCollections) {
			collection.notifyObjectNew(object);
		}
	}
	
	/**
	 * @param object
	 */
	private void notifyObjectNew(Class<? extends DomainObject> objectClass) {
		final AliveDomainObjectCollectionList controlledCollections = getAliveCollections();
		for (DomainObjectCollection<DomainObject> collection : controlledCollections) {
			collection.notifyObjectNew(objectClass);
		}
	}

	/**
	 * @param object
	 */
	final void notifyObjectRemove(DomainObject object) {
		final AliveDomainObjectCollectionList controlledCollections = getAliveCollections();
		for (DomainObjectCollection<DomainObject> collection : controlledCollections) {
			collection.notifyObjectRemove(object);
		}
	}

	/**
	 * 
	 * @param <D>
	 * @param criteria
	 * @return
	 */
	public final <D extends DomainObject> D getSingleObject(Criteria<D> criteria) {
		return this.objects.getSingleObject(criteria);
	}

	/**
	 * @param <D>
	 * @param domainObjectClass
	 */
	final <D extends DomainObject> D createBlank(Class<D> domainObjectClass) {
		return ReflectionUtils.create(domainObjectClass, this );
	}

	/**
	 * 
	 * @param <D>
	 * @param referece
	 * @return
	 */
	public final <D extends DomainObject> D resolve(
			Class<D> objectClass, long id ) {
		D result = this.objects.getObjectById( objectClass, id );
		if (logger.isDebugEnabled()) {
			logger.debug("resolve " + result + " by ref class " + objectClass + ", id " + id );
		}
		return result;
	}

	/**
	 * @return current transaction or throws exception if no transaction found.
	 */
	public final Transaction getTransaction() {
		synchronized( this.transactionChangeMutex ) {
			if (!hasTransaction()) {
				throw new CannotFindTransactionException();
			}
			return this.transaction;
		}
	}

	/**
	 * Return true if domain has transaction
	 */
	public final boolean hasTransaction() {
		synchronized( this.transactionChangeMutex ) {
			return this.transaction != null;
		}
	}

	/**
	 * Creates and return transaction. If transaction already exists than
	 * exception occured.
	 * 
	 * @return created transaction
	 */
	public final Transaction createTransaction() {
		synchronized( this.transactionChangeMutex ) {
			if (hasTransaction()) {
				Transaction oldTransaction = this.getTransaction();
				logger
						.error("Previous transaction is not commited. Trying to commit it. Transaction "
								+ oldTransaction);
				try {
					oldTransaction.commit();
				} finally {
					oldTransaction.dispose();
				}
				logger.warn("Old transaction was commited. " + oldTransaction);
			}
			this.transaction = new Transaction(this);
			return this.transaction;
		}
	}

	/**
	 * Create and return new unique id for domain object.
	 * 
	 * @param objectClass
	 *            domain object class
	 * @return created id.
	 */
	public final long createNewId(Class<? extends DomainObject> objectClass) {
		return this.tempIdGenerator.nextId();
	}

	/**
	 * @param criteria
	 * @return
	 */
	public final <D extends DomainObject> DomainObjectList<D> selectItems(
			Criteria<D> criteria) {
		return this.objects.selectItems(criteria);
	}

	/**
	 * Register collection in domain space. Collection will be notified about
	 * all significant changes in domain space.
	 * 
	 * @param collection
	 *            not null collection
	 */
	final void registerCollection(DomainObjectCollection collection) {
		this.aliveCollections.add(collection);
	}

	/**
	 * @param object
	 */
	final void markDirty(DomainObject object) {
		synchronized( this.transactionChangeMutex ) { 
			checkTransaction();
			this.objects.markDirty(object);
			// TODO notification after ditry.
		}
	}

	/**
	 * @param object
	 */
	final void markNew(DomainObject object) {
		synchronized( this.transactionChangeMutex ) {
			checkTransaction();
			this.objects.markNew(object);
			notifyObjectNew(object);
		}
	}

	/**
	 * 
	 */
	private final void checkTransaction() {
		if (!hasTransaction()) {
			throw new ObjectNotInTransactionException(this);
		}
	}

	/**
	 * @param object
	 */
	final void markRemoved(DomainObject object) {
		synchronized( this.transactionChangeMutex ) {
			checkTransaction();
			this.objects.markRemoved(object);
			notifyObjectRemove(object);
		}
	}

	/**
	 * 
	 */
	final void rollbackChanges() {
		synchronized( this.transactionChangeMutex ) {
			this.objects.rollbackChanges();
			transactionDisposed();
		}
	}

	/**
	 * 
	 */
	final void commitChanges() throws CommitFailedException {
		synchronized( this.transactionChangeMutex ) {
			this.objects.commitChanges();		
			transactionDisposed();
		}
	}

	/**
	 * @param transaction
	 */
	private void transactionDisposed() {
		if ( logger.isDebugEnabled() ) {
			logger.debug( "transaction diposed " + this.transaction );
		}
		this.transaction = null;
	}

	public final void resetCache() {
		this.objects.clear();
		synchronized( this.transactionChangeMutex ) {
			this.aliveCollections.clear();
		}
	}

	/**
	 * @return
	 */
	public final EditingScope createEditingScope() {
		return new EditingScope(this);
	}

	/**
	 * @param name
	 * @return
	 */
	public final <D extends AbstractHelper> D getHelper(Class<D> helperClass) {
		AbstractHelper helper = this.helperClassToHelperInstance
				.get(helperClass);
		if (helper == null) {
			throw new HelperNotFoundException(helperClass);
		}
		return helperClass.cast(helper);
	}

	protected final void registerHelper(AbstractHelper helper) {
		this.helperClassToHelperInstance.put(helper.getClass(), helper);
	}

	/**
	 * @param helper
	 */
	protected final void registerHelpers(AbstractHelper... helpers) {
		for (AbstractHelper helper : helpers) {
			registerHelper(helper);
		}
	}

	
	/**
	 * @param listener
	 */
	public final void addDomainChangesListener(DomainChangesListener listener) {
		this.objects.addDomainChangesListener(listener);
	}

	/**
	 * @param listener
	 */
	public final void removeDomainChangesListener(DomainChangesListener listener) {
		this.objects.removeDomainChangesListener(listener);
	}
	
	private void afterDataChanged(DataChangedEvent e) {
		synchronized( this.transactionChangeMutex ) {
			final boolean successfully = this.objects.mergeUpdate(e.getChangedData(), true );
			if ( !successfully ) {
				// TODO:#think about this mechanizm
				this.objects.clearChanges();
				this.transaction.obsolete();
			}		
			for( Class<? extends DomainObject> objectClass : e.getChangedData().getCreated() ) {
				notifyObjectNew(objectClass);
			}
		}
	}

	/**
	 * @return
	 */
	public final String getId() {
		return this.id;
	}

	/**
	 * @param object
	 * @return
	 */
	public final ObjectMark getMark(DomainObject object) {
		return this.objects.getMark( object );
	}

	/**
	 * 
	 */
	public static final class HelperNotFoundException extends
			IllegalArgumentException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 525789384481545892L;

		/**
		 * @param helperClass
		 */
		public HelperNotFoundException(Class helperClass) {
			super("Helper not found. Class " + helperClass);
		}
	}

	private final DataProviderListener dataProviderListener = new DataProviderListener() {
		public void dataChanged(DataChangedEvent e) {
			afterDataChanged(e);
		}
	};

	/**
	 * @param name
	 */
	public <T extends AbstractHelper> void registerHelper(Class<T> helperClass) {
		final T helper = ReflectionUtils.create(helperClass, this );
		registerHelper(helper);
	}
	

}