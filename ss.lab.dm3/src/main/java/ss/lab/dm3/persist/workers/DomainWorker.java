package ss.lab.dm3.persist.workers;

/**
 * @author Dmitry Goncharov
 */
public abstract class DomainWorker<C extends DomainWorkerContext> {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	public static final Object NOOP = new Object();
	
	/**
	 * 
	 * @return
	 * Supported returns:
	 * 
	 * * NOOP 
	 * * DataLoader
	 * * Transaction
	 * 
	 * 
	 */
	public abstract Object run(C context);
	
}
