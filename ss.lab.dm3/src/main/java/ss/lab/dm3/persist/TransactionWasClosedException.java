package ss.lab.dm3.persist;


/**
 * @author Dmitry Goncharov
 *
 */
public class TransactionWasClosedException extends DomainException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1712510972569944488L;


	/**
	 * @param transaction
	 */
	public TransactionWasClosedException(Transaction transaction) {
		super( "Transaction is closed. Transaction: " + transaction );
	}
}
