package ss.lab.dm3.blob.backend;

import ss.lab.dm3.connection.service.ServiceException;

/**
 * 
 * @author Dmitry Goncharov
 */
public class BlobException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1828631193563665107L;

	/**
	 * 
	 */
	public BlobException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BlobException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public BlobException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public BlobException(Throwable cause) {
		super(cause);
	}

}
