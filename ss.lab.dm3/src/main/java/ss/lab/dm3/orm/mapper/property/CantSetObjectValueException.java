package ss.lab.dm3.orm.mapper.property;

/**
 * @author Dmitry Goncharov
 */
public class CantSetObjectValueException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3118088991558510597L;

	/**
	 * @param abstractAccessor
	 * @param bean
	 * @param value
	 * @param ex
	 */
	public CantSetObjectValueException(Object context,
			Object bean, Object value, Throwable ex) {
		super( context + " can't set value " + value + " to object " + bean, ex );
	}
}
