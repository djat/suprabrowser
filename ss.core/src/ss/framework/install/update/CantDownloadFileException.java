/**
 * 
 */
package ss.framework.install.update;

/**
 * TODO replace downloads handler by this one
 */
public class CantDownloadFileException extends CantUpdateApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 136632280440504330L;


	/**
	 * @param message
	 * @param cause
	 */
	public CantDownloadFileException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CantDownloadFileException(String message) {
		super(message);
	}

}
