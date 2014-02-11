package ss.lab.dm3.persist.changeset;

/**
 * @author Dmitry Goncharov 
 */
public class TransactionChangeSetAdapter implements TransactionChangeSetListener {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.changeset.ITransactionChangeSetListener#accepted(ss.lab.dm3.persist.changeset.TransactionChangeSet)
	 */
	public void accepted(TransactionChangeSet transactionChangeSet) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Accepeted transanction change set " + transactionChangeSet );
		}
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.changeset.ITransactionChangeSetListener#onLocalConflict(ss.lab.dm3.persist.changeset.TransactionChangeSet)
	 */
	public void conflictedLocally(TransactionChangeSet transactionChangeSet) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Local conflict in transanction change set " + transactionChangeSet );
		}
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.changeset.ITransactionChangeSetListener#rejected(ss.lab.dm3.persist.changeset.TransactionChangeSet, java.lang.Throwable)
	 */
	public void rejected(TransactionChangeSet transactionChangeSet,
			Throwable cause) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Rejected transanction change set " + transactionChangeSet );
		}
	}

}
