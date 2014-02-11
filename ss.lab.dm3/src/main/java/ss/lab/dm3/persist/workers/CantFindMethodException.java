/**
 * 
 */
package ss.lab.dm3.persist.workers;

/**
 * @author Dmitry Goncharov
 */
public class CantFindMethodException extends DomainWorkerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7840536785266119695L;

	/**
	 * @param host
	 * @param methodName
	 */
	public CantFindMethodException(DomainWorkerHost host, String methodName) {
		super( "Can't find method " + methodName + " in " + host );
	}
	
}
