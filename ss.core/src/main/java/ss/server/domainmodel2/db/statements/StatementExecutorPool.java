package ss.server.domainmodel2.db.statements;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ss.server.domainmodel2.db.IStatementExecutor;

public class StatementExecutorPool {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(StatementExecutorPool.class);
	
	private final Set<StatementExecutor> freeExecutors = new HashSet<StatementExecutor>();

	private final String dbUrl;

	private int totalExecutorsCount = 0;

	/**
	 * @param dbUrl
	 */
	public StatementExecutorPool(final String dbUrl) {
		super();
		this.dbUrl = dbUrl;
	}

	public synchronized IStatementExecutor getStatementExecutor() {
		Iterator<StatementExecutor> iter = this.freeExecutors.iterator();
		StatementExecutor ret;
		if ( iter.hasNext() ) {
			ret = iter.next();
			this.freeExecutors.remove( ret );
		}
		else {
			++ this.totalExecutorsCount; 
			ret = new StatementExecutor( this, this.dbUrl );
			if ( logger.isDebugEnabled() ) { 
				logger.debug( "Creating new statement executor #" +  (this.totalExecutorsCount)  );
			}
		}
		ret.use();
		return ret;
	}

	/**
	 * @param executor
	 */
	public synchronized void release(StatementExecutor executor) {
		if ( !this.freeExecutors.contains(executor) ) {
			this.freeExecutors.add( executor );
		}
	}

	/**
	 * 
	 */
	public synchronized void disposeFree() {
		this.totalExecutorsCount -= this.freeExecutors.size();
		for( StatementExecutor executor : this.freeExecutors ) {
			executor.dispose();
		}
		this.freeExecutors.clear();
	}

	/**
	 * 
	 */
	public int getTotalExecutorsCount() {
		return this.totalExecutorsCount;		
	}
	
	public int getFreeExecutorsCount() {
		return this.freeExecutors.size();
	}
		
}
