/**
 * 
 */
package ss.lab.dm3.persist.workers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class DomainWorkReport {

	private final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final long startTime;
	
	private final List<String> workerResults = new ArrayList<String>();
	
	private DomainWorker<?> actualWorker = null;
	
	private long endTime;
	
	/**
	 * 
	 */
	public DomainWorkReport() {
		super();
		this.startTime = System.currentTimeMillis();
	}

	public void beginWorker( DomainWorker<?> worker ) {
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Begin worker " + worker );
		}
		this.actualWorker = worker; 
	}
	
	public void endWorker( Object result ) {
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Unit " + this.actualWorker + " finished with result " + result );
		}
		this.workerResults.add( this.actualWorker + ". Rresult " + result );
		this.actualWorker = null;		
	}

	public void finish() {
		this.endTime = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "Execution time ", this.endTime - this.startTime );
		for( String result : this.workerResults  ) {
			tsb.append( result );	
		}
		return tsb.toString();
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int getWorkersCount() {
		return this.workerResults.size();
	}
	
}
