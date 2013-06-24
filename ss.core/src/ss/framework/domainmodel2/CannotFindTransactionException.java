/**
 * 
 */
package ss.framework.domainmodel2;

import ss.common.UnexpectedRuntimeException;

/**
 *
 */
public class CannotFindTransactionException extends UnexpectedRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8296261303516699723L;

	/**
	 * @param message
	 */
	public CannotFindTransactionException() {
		super("Default domain object constructor cannot find transaction.");
	}

	
}
