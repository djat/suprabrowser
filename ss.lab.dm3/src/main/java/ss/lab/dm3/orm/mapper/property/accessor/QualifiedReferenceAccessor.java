/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.accessor;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.QualifiedReference;

/**
 * @author Dmitry Goncharov
 */
public class QualifiedReferenceAccessor extends AbstractReferenceAccessor {


	/**
	 * @param referenceAccessor
	 * @param name
	 * @param targetObjectClazz
	 */
	public QualifiedReferenceAccessor(MethodAccessor referenceAccessor, String name,
			Class<? extends MappedObject> targetObjectClazz) {
		super(referenceAccessor, name, targetObjectClazz);
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
		QualifiedReference<?> referenceToTarget;
		Long targetId;
		if ( value == null ) {
			referenceToTarget = null;
			targetId = null;
		}
		else if ( value instanceof QualifiedReference ) {
			referenceToTarget = (QualifiedReference<? extends MappedObject>) value;
			targetId = referenceToTarget.getTargetId();
		}
		else if ( this.targetObjectClazz.isInstance( value ) ) {
			referenceToTarget = QualifiedReference.wrap( this.targetObjectClazz.cast(value) );
			targetId = referenceToTarget.getTargetId();
		}
		else if ( value instanceof QualifiedObjectId ) {
			QualifiedObjectId<? extends MappedObject> qualifiedId = (QualifiedObjectId<? extends MappedObject>) value;
			targetId = qualifiedId.getId();
			referenceToTarget = QualifiedReference.wrap( qualifiedId );
		}
		// else if ( value instanceof Long ) {} 
		else {
			throw new IllegalArgumentException( "Unsupported value " + value );			
		}
		super.setValue(mappedBean, referenceToTarget);
		afterValueSetted(mappedBean, targetId);
	}

	@Override
	protected Long getTargetObjectId(MappedObject mappedBean) {
		final Object value = getValue(mappedBean);
		return value != null ? ((QualifiedReference<?>)value).getTargetId() : null;
	}
	
}
