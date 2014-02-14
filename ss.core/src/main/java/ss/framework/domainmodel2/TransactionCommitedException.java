/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class TransactionCommitedException extends IllegalStateException {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 747344557779265878L;

	
	/**
	 * @param transaction
	 */
	public TransactionCommitedException(Transaction transaction) {
		super( "Transaction commited " + transaction );
	}
}
