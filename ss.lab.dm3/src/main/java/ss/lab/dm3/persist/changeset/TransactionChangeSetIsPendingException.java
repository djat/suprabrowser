package ss.lab.dm3.persist.changeset;

import ss.lab.dm3.persist.DomainException;

/**
 * @author Dmitry Goncharov 
 */
public class TransactionChangeSetIsPendingException extends DomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -488041125195046949L;
	
	/**
	 * @param transactionChangeSet
	 */
	public TransactionChangeSetIsPendingException(TransactionChangeSet transactionChangeSet) {
		super( "Transaction change set " + transactionChangeSet + " is pending response from server." );
	}

}
