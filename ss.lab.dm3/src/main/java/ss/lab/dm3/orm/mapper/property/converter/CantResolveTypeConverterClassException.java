package ss.lab.dm3.orm.mapper.property.converter;

/**
 * @author Dmitry Goncharov
 *
 */
public class CantResolveTypeConverterClassException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1948236206070578843L;

	/**
	 * @param className
	 * @param cause
	 */
	public CantResolveTypeConverterClassException(String className, Throwable cause) {
		super( "Can't find accessor class by " + className, cause);
	}

	
}
