package ss.lab.dm3.persist;

/**
 * @author Dmitry Goncharov
 */
public class CantChangeTransactionStateException extends DomainException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -281785589285948688L;

	/**
	 * @param toState
	 * @param fromState
	 */
	public CantChangeTransactionStateException(TransactionState toState,
			TransactionState fromState) {
		super( "Can't change transaction state to " + toState + " from " + fromState );
	}
}
