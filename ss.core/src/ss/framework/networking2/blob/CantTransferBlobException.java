/**
 * 
 */
package ss.framework.networking2.blob;

/**
 *
 */
public class CantTransferBlobException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3298369999701368755L;

	/**
	 * @param message
	 * @param cause
	 */
	public CantTransferBlobException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CantTransferBlobException(String message) {
		super(message);
	}

	
	
}
