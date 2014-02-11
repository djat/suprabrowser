package ss.lab.dm3.persist;

public class TransactionConfclictedException extends DomainException {



	/**
	 * 
	 */
	private static final long serialVersionUID = 8098819755718062798L;

	/**
	 * @param transaction
	 */
	public TransactionConfclictedException(Transaction transaction) {
		super( "Transaction is conflicted. Transaction: " + transaction );
	}
}
