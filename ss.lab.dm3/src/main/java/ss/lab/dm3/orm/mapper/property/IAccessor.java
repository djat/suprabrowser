package ss.lab.dm3.orm.mapper.property;


/**
 * @author Dmitry Goncharov
 */
public interface IAccessor {

	Object getValue( Object bean ) throws CantGetObjectValueException;
	
	void setValue( Object bean, Object value ) throws CantSetObjectValueException;

	void refresh(Object bean);

	void resetToDefault(Object bean);  
}
