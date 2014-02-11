/**
 * 
 */
package ss.lab.dm3.persist;

import ss.lab.dm3.connection.Waiter;

/**
 * 
 * @author Dmitry Goncharov
 * 
 */
public abstract class DomainLockStrategy implements IDomainLockStrategy {
	
	private Domain domain = null;
	
	/**
	 * @param domain
	 */
	public DomainLockStrategy() {
		super();
	}

	public synchronized void install(Domain domain) {
		if ( this.domain != null ) {
			throw new IllegalStateException( "Strategy already installed to " + this.domain );
		}
		this.domain = domain;
	}
	
	
	public abstract void executeFromNotDomainThread(Runnable runnable);

	/**
	 * @return
	 */
	protected synchronized final Domain getDomain() {
		return this.domain;
	}
	
	/**
	 * @return
	 */
	protected final synchronized Domain checkAndGetDomain() {
		if ( this.domain == null ) {
			throw new IllegalStateException( "Strategy " + this + " is not installed" );
		}
		return this.domain;
	}

	public abstract Waiter createWaiter();

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.IDomainLockStrategy#uninstall()
	 */
	public Domain uninstall() {
		Domain domain = checkAndGetDomain();
		this.domain = null;
		return domain;
	}

}
