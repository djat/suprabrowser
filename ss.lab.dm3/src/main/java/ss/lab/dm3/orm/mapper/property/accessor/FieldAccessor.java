package ss.lab.dm3.orm.mapper.property.accessor;

import java.lang.reflect.Field;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.mapper.property.CantGetObjectValueException;
import ss.lab.dm3.orm.mapper.property.CantSetObjectValueException;

/**
 * @author Dmitry Goncharov
 */
public class FieldAccessor extends AbstractAccessor {

	private final Field field;
	
	/**
	 * @param field
	 */
	public FieldAccessor(Field field) {
		super();
		this.field = field;
		// TODO more intelligent switch to public access
		this.field.setAccessible( true );
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.mapper.IAccessor#getValue(java.lang.Object)
	 */
	public Object getValue(Object bean) throws CantGetObjectValueException {
		try {
			return this.field.get(bean);
		} catch (IllegalArgumentException ex) {
			throw new CantGetObjectValueException( this, bean, ex );
		} catch (IllegalAccessException ex) {
			throw new CantGetObjectValueException( this, bean, ex );
		}
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.mapper.IAccessor#setValue(java.lang.Object, java.lang.Object)
	 */
	public void setValue(Object bean, Object value) throws CantSetObjectValueException {
		try {
			this.field.set(bean, value);
		} catch (IllegalArgumentException ex) {
			throw new CantSetObjectValueException( this, bean, value, ex );
		} catch (IllegalAccessException ex) {
			throw new CantSetObjectValueException( this, bean, value, ex );
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb
		.append( "field", this.field )
		.toString();
	}

	
}
