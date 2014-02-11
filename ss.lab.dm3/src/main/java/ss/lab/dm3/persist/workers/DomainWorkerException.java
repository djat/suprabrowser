package ss.lab.dm3.persist.workers;

import ss.lab.dm3.persist.DomainException;

/**
 * @author Dmitry Goncharov
 */
public class DomainWorkerException extends DomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5795572229757341968L;

	/**
	 * @param ex
	 */
	public DomainWorkerException(Throwable ex) {
		super( "Domain worker failed", ex );
	}

	/**
	 * 
	 */
	public DomainWorkerException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DomainWorkerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DomainWorkerException(String message) {
		super(message);
	}
	
}
