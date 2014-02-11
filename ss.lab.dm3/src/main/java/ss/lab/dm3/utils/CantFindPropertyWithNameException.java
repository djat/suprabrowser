package ss.lab.dm3.utils;

/**
 * @author Dmitry Goncharov
 *
 */
public class CantFindPropertyWithNameException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7722420519655497011L;

	public CantFindPropertyWithNameException(Object context, String name) {
		super( "Can't find accessor with name " + name + " in " + context );
	}
}
