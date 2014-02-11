/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.accessor;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.OrmManagerResolveHelper;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * Works as bridge from Id to Object
 * 
 * @author Dmitry Goncharov
 */
public class NativeReferenceAccessor extends AbstractReferenceAccessor {

	/**
	 * @param referenceAccessor
	 * @param name
	 * @param targetObjectClazz
	 */
	public NativeReferenceAccessor(MethodAccessor referenceAccessor, String name,
			Class<? extends MappedObject> targetObjectClazz) {
		super(referenceAccessor, name, targetObjectClazz);
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see ss.lab.dm3.orm.mapper.accessor.MethodAccessor#getValue(java.lang.Object)
//	 */
//	@Override
//	public Object getValue(Object bean) throws CantGetObjectValueException {
//		MappedObject mappedObject = (MappedObject) super.getValue(bean);
//		if ( mappedObject == null ) {
//			return null;
//		}
//		final OrmManager manager = OrmManagerResolveHelper.resolve((MappedObject)bean);
//		return manager.getQualifiedObjectId(mappedObject);
//	}
		
	@Override
	protected Long getTargetObjectId(MappedObject mappedBean) {
		MappedObject mappedObject = (MappedObject) super.getValue(mappedBean);
		return mappedObject != null ? mappedObject.getId() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.orm.mapper.accessor.MethodAccessor#setValue(java.lang.Object,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object bean, Object value) {
		final MappedObject mappedBean = (MappedObject) bean;
		final OrmManager manager = OrmManagerResolveHelper.resolve(mappedBean);
		MappedObject targetObject;
		Long targetId;
		if ( value == null ) {
			targetObject = null;
			targetId = null;
		}
		else if ( this.targetObjectClazz.isInstance( value ) ) {
			targetObject = this.targetObjectClazz.cast(value);
			targetId = targetObject.getId();
		}
		else if ( value instanceof QualifiedObjectId ) {
			QualifiedObjectId<? extends MappedObject> qualifiedId = (QualifiedObjectId<? extends MappedObject>) value;
			targetId = qualifiedId.getId();
			final Class<? extends MappedObject> objectClazz = qualifiedId.getObjectClazz();
			if ( !this.targetObjectClazz.isAssignableFrom( objectClazz ) ) {
				throw new IllegalArgumentException( "Invalid qualififed object id " + qualifiedId + ". Expected id of " + this.targetObjectClazz );
			}
			targetObject = manager.resolve(objectClazz ,targetId);
		}
		else if ( value instanceof Long ) {
			targetId = (Long) value;
			targetObject = manager.resolve( this.targetObjectClazz, targetId);
		}
		else {
			throw new IllegalArgumentException( "Unsupported value " + value );			
		}
		super.setValue(mappedBean, targetObject);
		afterValueSetted(mappedBean, targetId);
	}

}
