/**
 * 
 */
package ss.framework.networking2.blob;

/**
 *
 */
public class DownloadRequestRefusedException extends CantTransferBlobException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5163911755380690056L;

	/**
	 * @param description
	 */
	public DownloadRequestRefusedException(String cause) {
		super( "Remote peer refuse download request. Cause: " + cause );
	}

}
