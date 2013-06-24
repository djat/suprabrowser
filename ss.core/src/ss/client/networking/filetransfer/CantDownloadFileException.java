package ss.client.networking.filetransfer;

public class CantDownloadFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 157568027629914300L;

	/**
	 * 
	 */
	public CantDownloadFileException() {
		super();
	}

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

	/**
	 * @param cause
	 */
	public CantDownloadFileException(Throwable cause) {
		super(cause);
	}

	
}
