package ss.lab.dm3.persist.changeset;


import ss.lab.dm3.connection.CallbackHandler;
import ss.lab.dm3.connection.CallbackHandlerException;
import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.DomainObjectCollector;
import ss.lab.dm3.persist.IChangeSetHandler;
import ss.lab.dm3.persist.ObjectController;
import ss.lab.dm3.persist.service.DataProviderAsync;
import ss.lab.dm3.utils.ListenerList;
import ss.lab.dm3.utils.SimpleRuntimeIdGenerator;

/**
 * @author Dmitry Goncharov 
 */
public class TransactionChangeSet extends ChangeSet {

	private final static SimpleRuntimeIdGenerator ID_GENERATOR = new SimpleRuntimeIdGenerator( "Tx" );
	
	private final Domain domain;

	private final ObjectsDataBackup objectsDataBackup;

	private final ListenerList<TransactionChangeSetListener> listeners = new ListenerList<TransactionChangeSetListener>(TransactionChangeSetListener.class);

	public enum State {
		ACTIVE, PENDING, LOCAL_CONFLICT, DISPOSED
	};

	private State state;

	/**
	 * @param mapperManager
	 */
	public TransactionChangeSet(Domain domain) {
		super( new ChangeSetId( null, ID_GENERATOR.qualifiedNextId() ), null );
		this.domain = domain;
		this.objectsDataBackup = new ObjectsDataBackup();
		this.state = State.ACTIVE;
	}

	/**
	 * 
	 */
	public void checkIsActive() {
		if (this.state != State.ACTIVE) {
			throw new TransactionChangeSetIsPendingException(this);
		}
	}

	/**
	 * @param domainObject
	 */
	public void regisrtyDirty(DomainObject domainObject) {
		checkIsActive();
		this.objectsDataBackup.backup(domainObject);
		this.dirtyObjects.add(domainObject);
	}

	/**
	 * @param domainObject
	 */
	public void registryNew(DomainObject domainObject) {
		checkIsActive();
		// Add object to backup to remember it for ObjectsDataBackup.hasDifferentData check
		this.objectsDataBackup.backup(domainObject);
		this.newObjects.add(domainObject);
	}

	/**
	 * @param domainObject
	 */
	public void registryRemoved(DomainObject domainObject) {
		checkIsActive();
		// Add object to backup to remember it for ObjectsDataBackup.hasDifferentData check
		this.objectsDataBackup.backupIfNotBackupped( domainObject );
		this.dirtyObjects.remove(domainObject);
		this.newObjects.remove(domainObject);
		this.removedObjects.add(domainObject);
	}

	/**
	 * @param changeSetHandler
	 */
	public void revert(IChangeSetHandler changeSetHandler) {
		this.objectsDataBackup.restoreAll();
		this.dirtyObjects.copyAndCleanTo(changeSetHandler);
		this.removedObjects.copyAndCleanTo(changeSetHandler);
	}

	/**
	 * @param transactionChangeSetListener
	 */
	public void addTransactionChangeSetListener(
			TransactionChangeSetListener transactionChangeSetListener) {
		this.listeners.add(transactionChangeSetListener);
	}

	/**
	 * @param transactionChangeSetListener
	 */
	public void removeTransactionChangeSetListener(
			TransactionChangeSetListener transactionChangeSetListener) {
		this.listeners.remove(transactionChangeSetListener);
	}

	public boolean merge(ChangeSetMergerContext mergerContext) {
		for (DomainObject dirty : mergerContext.getIncomingChangeSet()
				.getDirtyObjects()) {
			if (isRegisteredWithSameId(dirty)) {
				if (this.objectsDataBackup.hasDifferentData(dirty)) {
					// Incoming change set contains data about object that are
					// differ from start data of edited object
					// We should decline local change set
					return false;
				} else {
					// We should remove this object from incoming change set
					// because it overwrite
					// edited object.
					// If change set will be rejected, then object data will be
					// same as incoming
					// If change set will be accepted, then object data will be
					// same as edited
					mergerContext.registryIncommingIntersection(dirty);
				}
			}
		}
		for (DomainObject newObject : mergerContext.getIncomingChangeSet()
				.getNewObjects()) {
			if (isRegisteredWithSameId(newObject)) {
				// Incoming change set contains new object that has same id as
				// edited object
				// We should decline local change set
				return false;
			}
		}
		for (DomainObject removedObject : mergerContext.getIncomingChangeSet()
				.getRemovedObjects()) {
			if (isRegisteredWithSameId(removedObject)) {
				// Incoming change set contains removed object that has same id
				// as edited object
				// We should decline local change set
				return false;
			}
		}
		return true;
	}

	/**
	 * Notify listeners about change set reject
	 * 
	 * @param ex
	 */
	private void reject(Throwable ex) {
		try {
			if (this.log.isWarnEnabled()) {
				this.log.warn( "Rejecting changes " + this );
			}
			this.listeners.getNotificator().rejected(this, ex);
		} finally {
			this.state = State.DISPOSED;
		}
	}

	/**
	 * Notify listeners about change set accept
	 */
	private void accept() {
		try {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Accepting changes. Listeners " + this.listeners );
			}
			this.listeners.getNotificator().accepted(this);
		} finally {
			this.state = State.DISPOSED;
		}
	}

	/**
	 * 
	 */
	public void localConflict() {
		if (this.state != State.PENDING && this.state != State.ACTIVE) {
			throw new IllegalStateException(
					"Unexpected transaction change set state " + this.state);
		}
		try {
			this.listeners.getNotificator().conflictedLocally(this);
		} finally {
			this.state = State.LOCAL_CONFLICT;
		}
	}

	/**
	 * @param callbackHandler
	 */
	public void beginCommit(ICallbackHandler callbackHandler) {
		checkIsActive();
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Begin commit with " + callbackHandler );
		}
		CommitHook hook = new CommitHook(callbackHandler);
		ICallbackHandler safeHook = this.domain.createDomainSafeProxy(hook);
		this.dirtyObjects.setState( ObjectController.State.PENDING );
		this.newObjects.setState( ObjectController.State.PENDING );
		this.removedObjects.setState( ObjectController.State.PENDING );
		this.state = State.PENDING;
		DataProviderAsync dataProvider = this.domain.getDataProvider();
		dataProvider.commitData( this.toDataChangeSet(), safeHook );
		if (this.log.isDebugEnabled()) {
			this.log.debug( "dataProvider.commitData called" );
		}
	}

	/**
	 * 
	 */
	private final class CommitHook extends CallbackHandler {
		/**
		 * 
		 */
		private final ICallbackHandler commitHandler;

		/**
		 * @param commitHandler
		 */
		private CommitHook(ICallbackHandler commitHandler) {
			this.commitHandler = commitHandler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.lab.dm3.snapshoots.CallbackHandler#onFail(java.lang.Throwable)
		 */
		@Override
		public void onFail(Throwable ex) {
			reject(ex);
			if (this.commitHandler != null) {
				this.commitHandler.onFail(ex);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.lab.dm3.snapshoots.CallbackHandler#onSuccess(java.lang.Object)
		 */
		@Override
		public void onSuccess(Object result) throws CallbackHandlerException {
			accept();
			if (this.commitHandler != null) {
				this.commitHandler.onSuccess(result);
			}
		}
	}

	/**
	 * @param collector
	 */
	public void collect(DomainObjectCollector<?> collector) {
		this.newObjects.collect(collector);
		this.dirtyObjects.collect(collector);
	}

	/**
	 * @param objectId
	 * @return
	 */
	public boolean isRemoved(QualifiedObjectId<? extends DomainObject> objectId) {
		return this.removedObjects.contains(objectId);
	}

	/**
	 * @param objectClazz
	 * @param id
	 * @return
	 */
	public boolean isRemoved(Class<? extends DomainObject> objectClazz, Long id) {
		return this.removedObjects.contains(objectClazz,id);
	}

	/**
	 * @param objectId
	 * @return
	 */
	public boolean contains(QualifiedObjectId<? extends DomainObject> objectId) {
		return this.newObjects.contains(objectId) ||
				this.dirtyObjects.contains(objectId) ||
				this.removedObjects.contains(objectId);
	}

}
