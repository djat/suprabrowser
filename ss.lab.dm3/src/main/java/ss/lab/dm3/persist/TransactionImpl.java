package ss.lab.dm3.persist;


import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.persist.ObjectController.State;
import ss.lab.dm3.persist.changeset.TransactionChangeSet;
import ss.lab.dm3.persist.changeset.TransactionChangeSetAdapter;

/**
 * @author Dmitry Goncharov
 * 
 * Wish list:
 * - Object version control (on the client and on the server) -
 * - Objects properties constraints on the client side (login field for example)
 * 
 */
final class TransactionImpl implements Transaction {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Domain domain;

	private final int generation;
	
	private final Repository repository;

	private TransactionChangeSet changeSet;

	private TransactionState state = TransactionState.ALIVE;

	/**
	 * @param domain
	 * @param generation
	 */
	public TransactionImpl(Domain domain, int generation) {
		super();
		this.domain = domain;
		this.generation = generation;
		this.repository = domain.getRepository();
		this.changeSet = this.repository.beginEdit();
		this.changeSet
				.addTransactionChangeSetListener(new TransactionChangeSetAdapter() {
					@Override
					public void accepted(
							TransactionChangeSet transactionChangeSet) {
						onAccept();
					}

					@Override
					public void conflictedLocally(
							TransactionChangeSet transactionChangeSet) {
						onConflict();
					}

					@Override
					public void rejected(
							TransactionChangeSet transactionChangeSet,
							Throwable cause) {
						this.log.error( "Transaction rejected. Change set" + transactionChangeSet, cause );
						onReject();
					}
				});
	}
	
	@Override
	protected void finalize() throws Throwable {
		if ( this.domain != null ) {
			if ( this.state.isEditable() ) {
				this.log.error( "Finalizing transaction that is editable " + this );
				try {
					this.domain.execute( new Runnable() {
						public void run() {
							rollback();
						} 
					} );
				}
				catch( Throwable ex ) {
					this.log.error( "Can't rollback transaction from finalizer", ex );
				}
			}
		}
		super.finalize();		
	}



	/**
	 * 
	 */
	private void onConflict() {
		checkTransaction( false );
		this.repository.revertTransactionChanges(this.changeSet);
		switchToState(TransactionState.CONFLICT_WITH_INCOMMING_CHANGES);
	}

	/**
	 * TODO we don't touch previous transaction, but may be should  
	 */
	private void onAccept() {
		checkTransaction( false );
		this.repository.applyTransactionChanges(this.changeSet);
		switchToState(TransactionState.COMMITED);
	}

	/**
	 * 
	 */
	private void onReject() {
		checkTransaction( false );
		this.repository.revertTransactionChanges(this.changeSet);
		switchToState(TransactionState.ROLLBACKED);		
	}

	/**
	 * @param shouldBeEditable 
	 * 
	 */
	void checkTransaction(boolean shouldBeEditable) {
		this.domain.checkDomain();
		if ( shouldBeEditable ) {
			if ( !this.state.isEditable() ) {
				if ( this.state == TransactionState.CONFLICT_WITH_INCOMMING_CHANGES ) {
					throw new TransactionConfclictedException(this);
				}
				else {
					throw new TransactionWasClosedException(this);
				}
			}
		}	
	}
	
	public boolean isEditable() {
		this.domain.checkDomain();
		return this.state.isEditable();
	}

	/**
	 * @param commitHandler
	 */
	public void beginCommit(final ICallbackHandler commitHandler) {
		checkTransaction( true );
		switchToState(TransactionState.BEGIN_COMMIT);
		this.changeSet.beginCommit(commitHandler);
	}

	/**
	 * 
	 */
	public void rollback() {
		checkTransaction( false );
		switchToState(TransactionState.ROLLBACKED);
		this.repository.revertTransactionChanges(this.changeSet);
	}

	/**
	 * @param domainObject
	 */
	void registryDirty(DomainObject domainObject) {
		checkTransaction( true );
		this.repository.unregistryClean(domainObject, true);
		this.changeSet.regisrtyDirty(domainObject);
		domainObject.ctrl.changeState(State.DIRTY);
	}

	/**
	 * @param domainObject
	 */
	void registryNew(DomainObject domainObject) {
		checkTransaction( true );
		this.repository.ensureObjectIsNotRegistered(domainObject);
		this.changeSet.registryNew(domainObject);
		domainObject.ctrl.setGeneration(this.generation);
		domainObject.ctrl.changeState(State.NEW);
	}

	/**
	 * @param domainObject
	 */
	void registryRemoved(DomainObject domainObject) {
		checkTransaction( true );
		this.repository.ensureObjectIsRegistered(domainObject);
		if (domainObject.ctrl.isNew()) {
			// Remove it from changes
			this.changeSet.remove(domainObject);
			// Make it detached
			// TODO: should be DETACHED deleted objects that was new.
			domainObject.ctrl.changeState(State.DETACHED);
		} else {
			// Remove from clean
			this.repository.unregistryClean(domainObject, false);
			this.changeSet.registryRemoved(domainObject);
			domainObject.ctrl.changeState(State.REMOVED);
		}
	}

	/**
	 * @param conflictWithIncommingChanges
	 */
	private void switchToState(TransactionState nextState) {
		this.state.checkSwitchTo(nextState);
		this.state = nextState;
	}

	/**
	 * 
	 */
	public void beginCommit() {
		beginCommit( null );
	}

	public void dispose() {
		this.domain.checkDomain();
		if ( isEditable() ) {			
			this.log.warn( "Transaction is alive. Going to roll back it" );
			rollback();
		}
	}

	/**
	 * 
	 */
	public void commit() {
		CallbackResultWaiter waiter = new CallbackResultWaiter();
		beginCommit( waiter );
		waiter.waitToResult();
	}
	
	public static TransactionImpl getTransactionImpl( Transaction transaction ) {
		if ( transaction == null ) {
			return null;
		}
		if ( transaction instanceof TransactionImpl ) {
			return (TransactionImpl) transaction;
		}
		else {
			return ((TransactionProxy) transaction).getImplementation();
		}
	}
	
}
