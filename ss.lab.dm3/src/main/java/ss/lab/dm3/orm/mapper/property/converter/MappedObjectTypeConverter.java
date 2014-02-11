package ss.lab.dm3.orm.mapper.property.converter;

import java.io.Serializable;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.QualifiedUtils;

/**
 * @author Dmitry Goncharov 
 *
 */
public class MappedObjectTypeConverter<T extends MappedObject> extends TypeConverter<T> {
	
	public MappedObjectTypeConverter(Class<T> entityClazz) {
		super(entityClazz);
	}

	@Override
	public T fromString(String value) {
		if ( value == null || value.equals("") ) {
			return null;
		}
		else {
			Long id = Long.parseLong( value );
			return QualifiedUtils.toObject( this.type, id );
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T fromSerializable(Serializable loadValue) {
		QualifiedObjectId<T> qualifiedId = (QualifiedObjectId<T>) loadValue;
		return QualifiedUtils.toObject(qualifiedId);
	}

	@Override
	public Serializable toSerializable(Object value) {
		return value != null ? QualifiedUtils.toId(value) : null;
	}

	@Override
	public T fromObject(Object value) {
		if ( value instanceof Long ) {
			Long id = ((Long) value);
			return QualifiedUtils.toObject(this.type, id );
		}
		else {
			return super.fromObject(value);
		}
	}
	
	
}
