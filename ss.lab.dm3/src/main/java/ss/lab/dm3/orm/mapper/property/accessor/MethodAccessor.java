package ss.lab.dm3.orm.mapper.property.accessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ss.lab.dm3.orm.mapper.property.CantGetObjectValueException;
import ss.lab.dm3.orm.mapper.property.CantSetObjectValueException;

/**
 * @author Dmitry Goncharov
 */
public class MethodAccessor extends AbstractAccessor {

	protected final Method getter;
	
	protected final Method setter;

	/**
	 * @param getter
	 * @param setter
	 */
	public MethodAccessor(Method getter, Method setter) {
		super();
		this.getter = getter;
		this.setter = setter;
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.mapper.IAccessor#getValue(java.lang.Object)
	 */
	public Object getValue(Object bean) throws CantGetObjectValueException {
		try {
			return this.getter.invoke( bean );
		} catch (IllegalArgumentException ex) {
			throw new CantGetObjectValueException( this, bean, ex );
		} catch (IllegalAccessException ex) {
			throw new CantGetObjectValueException( this, bean, ex );
		} catch (InvocationTargetException ex) {
			throw new CantGetObjectValueException( this, bean, ex );
		}
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.mapper.IAccessor#setValue(java.lang.Object, java.lang.Object)
	 */
	public void setValue(Object bean, Object value) {
		try {
			this.setter.invoke(bean, value);
		} catch (IllegalArgumentException ex) {
			throw new CantSetObjectValueException( this, bean, value, ex );
		} catch (IllegalAccessException ex) {
			throw new CantSetObjectValueException( this, bean, value, ex );
		} catch (InvocationTargetException ex) {
			throw new CantSetObjectValueException( this, bean, value, ex ); 
		}
	}

	public Method getSetter() {
		return this.setter;
	}
	
	
}
