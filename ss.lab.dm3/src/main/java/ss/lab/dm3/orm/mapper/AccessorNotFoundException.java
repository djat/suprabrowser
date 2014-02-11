package ss.lab.dm3.orm.mapper;

/**
 * @author Dmitry Goncharov
 *
 */
public class AccessorNotFoundException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1600396233000742081L;


	/**
	 * @param objectMapper
	 * @param propertyName
	 */
	public AccessorNotFoundException(Object context,
			String propertyName) {
		super( "Can't find accessor in " + context + " with name " + propertyName );
	}
	
}
