/**
 * 
 */
package ss.lab.dm3.persist;

/**
 * @author Dmitry Goncharov
 */
public class DomainException extends RuntimeException {

	/**
	 * 
	 */
	public static final long serialVersionUID = 2425542663152303054L;

	/**
	 * 
	 */
	public DomainException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DomainException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DomainException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DomainException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}
