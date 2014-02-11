package ss.lab.dm3.orm.mapper.property.converter;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.QualifiedReference;
import ss.lab.dm3.orm.QualifiedUtils;

/**
 * @author Dmitry Goncharov 
 *
 */
public class QualifiedReferenceTypeConverter<T extends MappedObject> extends TypeConverter<QualifiedReference<T>> {
	
	private final Class<T> entityClazz;
	
	public QualifiedReferenceTypeConverter(Class<T> entityClazz) {
		super(QualifiedReference.wrap(entityClazz));
		this.entityClazz = entityClazz;
	}

	@Override
	public QualifiedReference<T> fromString(String value) {
		if ( value == null || value.equals("") ) {
			return null;
		}
		else {
			Long id = Long.parseLong( value );
			return QualifiedReference.wrap( QualifiedUtils.toObject(this.entityClazz, id) );
		}
	}

	@Override
	public QualifiedReference<T> fromObject(Object value) {
		if ( value instanceof Long ) {
			Long id = ((Long) value);
			return QualifiedReference.wrap( QualifiedUtils.toObject(this.entityClazz, id) );
		}
		else if ( value instanceof QualifiedObjectId ) {
			final QualifiedObjectId<T> qualifiedObjectId = QualifiedObjectId.cast( this.entityClazz, (QualifiedObjectId<?>) value );
			return QualifiedReference.wrap( qualifiedObjectId );
		}
		else {
			return super.fromObject(value);
		}
	}
	
	
	
}
