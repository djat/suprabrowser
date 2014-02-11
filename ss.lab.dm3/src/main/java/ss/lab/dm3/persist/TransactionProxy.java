package ss.lab.dm3.persist;

import ss.lab.dm3.connection.ICallbackHandler;

public class TransactionProxy implements Transaction {

	private Transaction transaction;

	private Domain domain;
	
	private TransactionState state = TransactionState.ALIVE;
	
	/**
	 * @param transaction
	 */
	public TransactionProxy(Domain domain, Transaction transaction) {
		if ( !transaction.isEditable() ) {
			throw new IllegalArgumentException( "Transaction is not editable " + transaction );
		}
		this.domain = domain;
		this.transaction = transaction;
		throw new UnsupportedOperationException( "Not yet implemented" );
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.Transaction#beginCommit(ss.lab.dm3.connection.ICallbackHandler)
	 */
	public void beginCommit(ICallbackHandler commitHandler) {
		if ( commitHandler != null ) {
			throw new UnsupportedOperationException( "commitHandler is not yet supported for proxy transaction" );
		}
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.Transaction#beginCommit()
	 */
	public void beginCommit() {
		commit();
	}

	/**
	 * @param conflictWithIncommingChanges
	 */
	private void switchToState(TransactionState nextState) {
		this.state.checkSwitchTo(nextState);
		this.state = nextState;
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.Transaction#commit()
	 */
	public void commit() {
		this.domain.checkDomain();
		switchToState( TransactionState.BEGIN_COMMIT );
		// update domain
		
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.Transaction#dispose()
	 */
	public void dispose() {
		this.domain.checkDomain();
		//this.disposed = true;
		
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.Transaction#isEditable()
	 */
	public boolean isEditable() {
		return this.state.isEditable();
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.Transaction#rollback()
	 */
	public void rollback() {
		getImplementation();
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 */
	public TransactionImpl getImplementation() {
		return TransactionImpl.getTransactionImpl( this.transaction );
	}

}
