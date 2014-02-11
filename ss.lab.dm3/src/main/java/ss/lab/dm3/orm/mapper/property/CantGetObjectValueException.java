package ss.lab.dm3.orm.mapper.property;

/**
 * @author Dmitry Goncharov
 */
public class CantGetObjectValueException extends RuntimeException {

	@SuppressWarnings("unused")
	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
		.getLog(CantGetObjectValueException.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 2051121653210176176L;

	/**
	 * @param abstractAccessor
	 * @param object
	 * @param ex
	 */
	public CantGetObjectValueException(Object context,
			Object bean, Throwable ex) {
		super( objectToString(context)  + " cant get value from " + objectToString(bean), ex );
	}

	/**
	 * @param object
	 * @return
	 */
	private static String objectToString(Object object) {
		try {
			return object != null ? object.toString() : null;
		}
		catch(RuntimeException ex ) {
			// log.warn( "Can't convert object to string " + object );
			return object != null ? object.getClass().getSimpleName() : null;
		}
	}
	
}
