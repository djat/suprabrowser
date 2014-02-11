package ss.lab.dm3.orm.mapper.property.accessor;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.OrmManagerResolveHelper;

public abstract class AbstractReferenceAccessor extends MethodAccessor {

	protected final String name;

	protected final Class<? extends MappedObject> targetObjectClazz;

	/**
	 */
	public AbstractReferenceAccessor(MethodAccessor referenceAccessor, String name,
			Class<? extends MappedObject> targetObjectClazz) {
		super(referenceAccessor.getter, referenceAccessor.setter);
		this.name = name;
		this.targetObjectClazz = targetObjectClazz;
	}	
	
	/**
	 * @param mappedBean
	 * @param manager
	 * @param targetId
	 */
	protected final void afterValueSetted(final MappedObject mappedBean, Long targetId) {
		final OrmManager manager = OrmManagerResolveHelper.resolve(mappedBean);
		if ( manager.isObjectManaged( mappedBean ) ) {
			manager.referenceChanged(mappedBean, this.name, targetId);
		}
	}

	protected abstract Long getTargetObjectId(MappedObject mappedBean);	

	@Override
	public void refresh(Object bean) {
		super.refresh(bean);
		final MappedObject mappedBean = (MappedObject)bean;
		OrmManager manager = OrmManagerResolveHelper.resolve( mappedBean );
		if ( manager.isObjectManaged( mappedBean ) ) {
			afterValueSetted( mappedBean, getTargetObjectId(mappedBean) );
		}
	}


	/**
	 * @return
	 */
	public final String getName() {
		return this.name;
	}

	@Override
	public void resetToDefault(Object bean) {
		super.resetToDefault(bean);
		// TODO Introduce default values for objects
		setValue(bean, null);	
	}
	
}
