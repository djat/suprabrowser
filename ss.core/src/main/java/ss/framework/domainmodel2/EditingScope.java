/**
 * 
 */
package ss.framework.domainmodel2;

import ss.framework.domainmodel2.DomainSpaceObjects.CommitFailedException;


/**
 *
 */
public final class EditingScope {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EditingScope.class);
	
	private final static boolean SUPRESS_ERRORS = false;
	
	private Transaction transactionToCommit = null;
	/**
	 * @param spaceOwner
	 */
	public EditingScope(final AbstractDomainSpace spaceOwner) {
		super();
		if ( !spaceOwner.hasTransaction() ) {
			this.transactionToCommit = spaceOwner.createTransaction();			
		}
	}

	/**
	 * 
	 */
	public final void dispose() throws CommitFailedException {
		if ( this.transactionToCommit != null ) {
			try {
				this.transactionToCommit.commit();
			}
			catch( CommitFailedException ex ) {
				if ( SUPRESS_ERRORS ) {
					logger.error( "Transaction commit failed", ex );
				}
				else {
					throw ex;
				}
			}
			this.transactionToCommit = null;
		}		
	}
	
	
	
	

}
