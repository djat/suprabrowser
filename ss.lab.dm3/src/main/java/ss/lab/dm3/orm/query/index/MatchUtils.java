package ss.lab.dm3.orm.query.index;

import java.io.Serializable;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedUtils;
import ss.lab.dm3.orm.mapper.property.Property;
import ss.lab.dm3.orm.mapper.property.converter.TypeConverter;

/**
 * @author Dmitry Goncharov
 *
 */
public class MatchUtils {

	private final static Serializable NULL_VALUE = new NullValue();
	
	/**
	 * TODO we can use property in future to more clever comparison 
	 * @param value
	 * @return
	 */
	public static Object getMatchable(Object value, Property<?> property) {
		if ( value == null ) {
			return null;
		}
		final TypeConverter<?> typeConverter = property.getTypeConverter();
		if ( typeConverter.isInstance( value ) ) {
			return value;
		}
		else {
			return typeConverter.fromObject(value);
		}		
	}

	/**
	 * @param object
	 * @return
	 */
	public static Serializable getSerializable(Object value) {
		if ( value instanceof MappedObject ) {
			return QualifiedUtils.toId(value);
		}
		else {
			return (Serializable) value;
		}
	}
	
	public static Object getNotNullMatchable(Object value, Property<?> property) {
		if ( value == null ) {
			return NULL_VALUE;
		}
		else {
			return getMatchable(value,property);
		}
	}

	/**
	 *
	 */
	private static final class NullValue implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7268038701517692685L;
	}

}
