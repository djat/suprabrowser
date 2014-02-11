/**
 * 
 */
package ss.lab.dm3.persist.workers;

/**
 * @author Dmitry Goncharov
 */
public class InvalidDomainWorkerResultException extends DomainWorkerException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7316076601269953469L;

	/**
	 * @param worker
	 * @param ret
	 */
	public InvalidDomainWorkerResultException(DomainWorker<?> worker, Object ret) {
		super( "Unknown unit result " + ret + " from " + worker );
	}
	
}
