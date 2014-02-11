package ss.lab.dm3.persist;


/**
 * @author Dmitry Goncharov 
 */
public class TransactionState {

	public static final TransactionState ALIVE;
	public static final TransactionState BEGIN_COMMIT;
	public static final TransactionState ROLLBACKED; 
	public static final TransactionState COMMITED;
	public static final TransactionState CONFLICT_WITH_INCOMMING_CHANGES;

	static {
		 ALIVE = new TransactionState( "Alive", true );
		 BEGIN_COMMIT = new TransactionState( "Transaction commit was began.", false );
		 ROLLBACKED = new TransactionState("Transaction was rollbacked.", false );
		 COMMITED = new TransactionState( "Commited", false );
		 CONFLICT_WITH_INCOMMING_CHANGES = new TransactionState("Transaction was closed because of conflict with incoming change set.", false );
		 ALIVE.setNextStates( BEGIN_COMMIT, ROLLBACKED, CONFLICT_WITH_INCOMMING_CHANGES );
		 BEGIN_COMMIT.setNextStates( COMMITED, ROLLBACKED, CONFLICT_WITH_INCOMMING_CHANGES );
	}
	
	private final String message;
	
	private final boolean editable;
	
	private TransactionState [] nextStates = null;
	
	/**
	 * @param string
	 */
	private TransactionState(String message, boolean editable) {
		this.message = message;
		this.editable = editable;
	}
	
	public boolean isEditable() {
		return this.editable;
	}

	/**
	 * @param nextStates
	 */
	private void setNextStates(TransactionState ... nextStates ) {
		this.nextStates = nextStates;
	}

	public void checkSwitchTo( TransactionState nextState ) {
		if ( this.nextStates != null ) {
			for ( TransactionState possibleNextState : this.nextStates ) {
				if ( possibleNextState == nextState ) {
					return;
				}
			}
		}
		throw new CantChangeTransactionStateException( nextState, this );
	}

	@Override
	public String toString() {
		return this.message;
	}

}