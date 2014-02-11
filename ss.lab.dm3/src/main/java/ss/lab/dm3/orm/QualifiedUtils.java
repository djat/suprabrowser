package ss.lab.dm3.orm;

import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.property.Property;

public class QualifiedUtils {
	
	public static QualifiedObjectId<?> toId(Object value, Property<?> property ) {
		if ( value == null ) {
			return null;
		}
		if ( property.isReference() ) {
			return (QualifiedObjectId<?>) property.getTypeConverter().fromObject( value );
		}
		else {
			throw new IllegalArgumentException( "Property is not reference " + property + " value " + value );
		}
	}
	
	public static <T extends MappedObject> QualifiedObjectId<? extends T> toId(Class<T> baseClazz, Long id ) {
		T obj = toObject(baseClazz, id );
		final OrmManager orm = OrmManagerResolveHelper.resolve();
		return orm.getQualifiedObjectId(obj);
	}

	public static <T> QualifiedObjectId<T> toId(Object value, Class<T> objectClazz ) {
		final QualifiedObjectId<?> qualifiedId = toId(value);
		return QualifiedObjectId.cast(objectClazz, qualifiedId);
	}
	
	public static QualifiedObjectId<?> toId(Object value) {
		if ( value == null ) {
			return null;
		}
		else if ( value instanceof QualifiedObjectId<?> ) {
			return ((QualifiedObjectId<?>) value);
		}
		else if ( value instanceof QualifiedReference<?> ) {
			return ((QualifiedReference<?>) value).getTargetQualifiedId();
		}
		else if ( value instanceof MappedObject ) {
			MappedObject mappedObject = (MappedObject) value;
			final OrmManager orm = OrmManagerResolveHelper.resolve( mappedObject );
			return orm.getQualifiedObjectId( mappedObject );
		}
		else {
			throw new IllegalArgumentException( "Unsupported value " + value );
		}
	}

	public static <T extends MappedObject> T toObject(Class<T> baseClazz, Long id) {
		if ( id == null ) {
			return null;
		}
		if ( baseClazz == null ) {
			throw new NullPointerException( "baseClazz" );
		}
		final OrmManager orm = OrmManagerResolveHelper.resolve();
		return orm.resolve(baseClazz, id);
	}

	/**
	 * @param qualifiedId
	 * @return
	 */
	public static <T extends MappedObject> T toObject(
			QualifiedObjectId<T> qualifiedId) {
		return qualifiedId != null ? toObject(qualifiedId.getObjectClazz(), qualifiedId.getId()) : null;
	}
	
	public static MappedObject toObject( Object value ) {
		if ( value == null ) {
			return null;
		}
		else if ( value instanceof MappedObject ) {
			return (MappedObject) value;
		}
		else if ( value instanceof QualifiedReference<?> ) {
			return ((QualifiedReference<?>) value).get();
		}
		else if ( value instanceof QualifiedObjectId<?> ) {
			QualifiedObjectId<?> qualifiedId = (QualifiedObjectId<?>) value;
			return toObject( QualifiedObjectId.cast( MappedObject.class, qualifiedId ) );
		}
		else {
			throw new IllegalArgumentException( "Unsupported value " + value );
		}
	}
	
	public static final Class<? extends MappedObject> resolveClass(String qualifier) {
		final BeanMapper<?> mapper = getOrmManager().getBeanMapperProvider().get( qualifier );
		return mapper.getObjectClass();
	}
	
	public static final String resolveQualifier( MappedObject obj ) {
		final BeanMapper<?> mapper = getOrmManager().getBeanMapperProvider().get( obj );
		return mapper.getEntityName();
	}
	
	public static final String resolveQualifier( Class<? extends MappedObject> objectClazz ) {
		final BeanMapper<?> mapper = getOrmManager().getBeanMapperProvider().get( objectClazz );
		return mapper.getEntityName();
	}
	
	public static final OrmManager getOrmManager() {
		return OrmManagerResolveHelper.resolve();
	}
	
}
