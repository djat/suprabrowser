package ss.lab.dm3.persist.space;

import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainLoader;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.IObjectMatcher;
import ss.lab.dm3.persist.Query;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class QuerySpace extends Space {
	
	private final IObjectMatcher expandMatcher;
	
	public QuerySpace() {
		this( null );
	}
	
	/**
	 * @param expandMatcher
	 */
	public QuerySpace(IObjectMatcher expandMatcher) {
		super();
		this.expandMatcher = expandMatcher;
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.space.SynchronizedSpace#match(ss.lab.dm3.persist.DomainObject)
	 */
	@Override
	public boolean shouldExpandBy(DomainObject object) {
		return this.expandMatcher != null && this.expandMatcher.match( object );
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.space.Space#beginLoad(ss.lab.dm3.persist.Domain, ss.lab.dm3.connection.ICallbackHandler)
	 */
	public void beginLoad(Domain domain, Query query, ICallbackHandler handler) {
		DomainLoader loader = new DomainLoader( query, this );
		loader.beginLoad( domain, handler );
	}
		
}
