package ss.lab.dm3.orm.mapper.property.converter;

import java.io.Serializable;

/**
 * 
 * @author Dmitry Goncharov
 *
 * @param <T>
 */
public class TypeConverter<T> {

	protected final Class<T> type;
	
	/**
	 * @param type
	 */
	public TypeConverter(Class<T> type) {
		super();
		this.type = type;
	}

	/**
	 * @param value
	 */
	public final T cast(Object value) {
		return this.type.cast( value );
	}
	
	public final boolean isInstance(Object value ) {
		return this.type.isInstance(value);
	}

	/**
	 * @param value
	 */
	public Serializable toSerializable(Object value) {
		return (Serializable) value;
	}

	/**
	 * @param loadValue
	 * @return
	 */
	public T fromSerializable(Serializable loadValue) {
		if (loadValue!= null && !this.type.isInstance(loadValue)) {
		    throw new ClassCastException( "Can't cast " + loadValue + " to " + this.type );
		}
		return this.type.cast(loadValue);
	}

	@SuppressWarnings("unchecked")
	public T fromString(String value) {
		if ( this.type.isEnum() ) {
			 return this.type.cast( Enum.valueOf( (Class)this.type, value ) ); 
		}
		else {
			if ( this.type == String.class ) {
				return cast( value );
			}
			else if ( value == null ) {
				return null;
			}
			else {
				throw new IllegalArgumentException( "Can't " + this + " convert " + value );
			}
		}
	}

	/**
	 * @param value
	 * @return
	 */
	public T fromObject(Object value) {
		if ( value == null ) {
			return null;
		}
		if ( this.type.isInstance(value) ) {
			return this.type.cast(value);
		}
		else if ( value instanceof String ) {
			return fromString( (String)value);
		}
		else if ( value instanceof Serializable ) {
			return fromSerializable( (Serializable) value );
		}
		else {
			throw new IllegalArgumentException( "Can't " + this + " convert " + value );
		}
	}
	
}
