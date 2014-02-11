package ss.lab.dm3.persist.script;

import java.io.Serializable;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.Query;
import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.persist.space.QuerySpace;

/**
 * Modification script designed to perform security safe domain modification.
 * Currently script designed to run on the client and verify on the server, but
 * it can be also executed on the server side.
 * 
 * Script life cycle [DRAFT] 
 * 1. Initialization and setup
 * 2. Run
 * 2.1. Get lock (Optional)
 * 2.2. Begin transaction and set transaction verifier (TODO)
 * 2.3. Do action
 * 2.4. Commit
 * 3. Server side data commit.
 * 3.1. Release lock
 * 3.2. Check commit result by verifier 
 *    
 * 
 * @author Dmitry Goncharov
 */
public abstract class ModifyScript implements IScript, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1752258197422074528L;
	
	private transient Domain domain;
	
	/**
	 * @return
	 */
	public Domain getDomain() {
		return this.domain;
	}
	
	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	@Deprecated
	public <T extends DomainObject> ScriptDomainObjectReference<T> createReference() {
		throw new UnsupportedOperationException( "see ScriptDomainObjectReference");
	}

	@Deprecated
	public <T extends DomainObject> ScriptDomainObjectReference<T> createReference(T object) { 
		throw new UnsupportedOperationException( "see ScriptDomainObjectReference");
	}
	
	public final void beginPerform() {
		beginPerform(null);
	}
	
	public final void beginPerform(final ICallbackHandler callbackHandler ) {
		Query query = getFetchQuery();
		if ( query != null ) {
			if ( this.domain.getDomainThread() == Thread.currentThread() ) {
				// TODO implement beginRun with fetch script in domain thread
				throw new UnsupportedOperationException( "beginRun with fetch query in domain is not supported " + this );
			}
			// Load data 
			QuerySpace space = new QuerySpace();			
			CallbackResultWaiter fetchWaiter = new CallbackResultWaiter();
			space.beginLoad( this.domain, query, fetchWaiter );
			fetchWaiter.waitToResult();
			// Run perform
			this.domain.execute( new Runnable() {
				public void run() {
					performTransaction(callbackHandler);
				}
			});			
		}
		else {
			if ( this.domain.getDomainThread() == Thread.currentThread() ) {
				performTransaction(callbackHandler);
			}
			else {
				this.domain.execute( new Runnable() {
					public void run() {
						performTransaction(callbackHandler);
					}
				});
			}
		}
	}

	/**
	 * @param callbackHandler
	 */
	private void performTransaction(ICallbackHandler callbackHandler) {
		Transaction tx = this.domain.beginTrasaction();
		try {
			// TODO [dg] set up transaction bounds factory
			perform( this.domain );
			tx.beginCommit( callbackHandler );
		}
		finally {
			tx.dispose();
		}
	}
	
	protected Query getFetchQuery() {
		return null;
	}
	
	/**
	 * @param domain
	 */
	protected abstract void perform(Domain domain);
		
}
