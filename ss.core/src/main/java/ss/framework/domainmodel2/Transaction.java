/**
 * 
 */
package ss.framework.domainmodel2;

import ss.common.IdentityUtils;
import ss.framework.domainmodel2.DomainSpaceObjects.CommitFailedException;

/**
 *
 */
public final class Transaction {

	@SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Transaction.class);
	
	private String runtimeId = IdentityUtils.getNextRuntimeId(Transaction.class);
	
	private final AbstractDomainSpace spaceOwner; 
	
	private boolean disposed = false;
	
	private boolean commited = false;

	/**
	 * @param registryOwner
	 */
	protected Transaction(final AbstractDomainSpace spaceOwner) {
		super();
		this.spaceOwner = spaceOwner;		
	}
	
	public synchronized void dispose() {
		if ( this.disposed ) {
			return;
		}
		this.disposed = true;
		if ( !this.commited ) {
			this.spaceOwner.rollbackChanges();
		}		
	}

	public synchronized void commit() throws CommitFailedException{
		validate();
		checkDisposed();
		if ( this.commited ) {
			throw new TransactionCommitedException( this );
		}
		this.commited = true;
		this.spaceOwner.commitChanges();
		dispose();
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.ITransaction2#validate()
	 */
	public synchronized void validate() {
		checkDisposed();
		// TODO#implement		
	}
	
	private void checkDisposed() {
		if ( this.disposed  ) {
			throw new ObjectDisposedException( this );
		}
	}
	
	public final AbstractDomainSpace getSpaceOwner() {
		return this.spaceOwner;
	}

	/**
	 * @return the disposed
	 */
	public boolean isDisposed() {
		return this.disposed;
	}

	/**
	 * 
	 */
	void obsolete() {
		dispose();
		// TODO#implement data obsolete code
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Transaction " + this.runtimeId;
	}
	
	
	
}