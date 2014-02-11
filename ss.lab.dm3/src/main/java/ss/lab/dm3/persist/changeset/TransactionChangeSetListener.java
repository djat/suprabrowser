package ss.lab.dm3.persist.changeset;

/**
 * @author Dmitry Goncharov 
 */
public interface TransactionChangeSetListener {

	void accepted(TransactionChangeSet transactionChangeSet );

	/**
	 * @param transactionChangeSet
	 * @param cause
	 */
	void rejected(TransactionChangeSet transactionChangeSet, Throwable cause);
	
	
	void conflictedLocally(TransactionChangeSet transactionChangeSet);
}
