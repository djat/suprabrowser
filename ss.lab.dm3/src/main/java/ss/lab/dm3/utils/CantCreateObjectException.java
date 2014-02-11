package ss.lab.dm3.utils;

/**
 * @author Dmitry Goncharov
 *
 */
public class CantCreateObjectException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7049690350914584808L;

	/**
	 * @param objectClass
	 * @param cause
	 */
	public CantCreateObjectException(Class<?> objectClass,
			Throwable cause) {
		super( "Can't create domain object of class " + objectClass, cause );
	}
}
