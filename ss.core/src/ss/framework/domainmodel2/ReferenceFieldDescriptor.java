/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public class ReferenceFieldDescriptor<D extends DomainObject> extends LazyFieldDescriptor<ReferenceField<D>,D> {

	/**
	 * 
	 */
	static final int NULL_REFERENCE_ID = 0;
	
	private final Class<D> targetDomainObjectClass;

	/**
	 * @param domainObjectClass
	 * @param name
	 * @param fieldClass
	 * @param targetDomainObjectClass
	 */
	@SuppressWarnings("unchecked")
	public ReferenceFieldDescriptor(Class<? extends DomainObject> domainObjectClass, String name, Class<D> targetDomainObjectClass) {
		super(domainObjectClass, name, (Class)ReferenceField.class );
		this.targetDomainObjectClass = targetDomainObjectClass;
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.fields.LazyFieldDescriptor#resolve(ss.framework.domainmodel2.AbstractDomainSpace, java.lang.String)
	 */
	@Override
	public D resolve(AbstractDomainSpace space, String loadedStrValue) {
		long id = StringConvertor.stringToLong(loadedStrValue, NULL_REFERENCE_ID );
		return space.resolve(this.targetDomainObjectClass, id );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.fields.FieldDescriptor#getValueToSave(ss.framework.domainmodel2.fields.Field)
	 */
	@Override
	protected String getValueToSave(ReferenceField<D> field) {
		D targetObject = field.get();
		return String.valueOf( targetObject != null ? targetObject.getId() : NULL_REFERENCE_ID );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.FieldDescriptor#getActualValue(ss.framework.domainmodel2.Field)
	 */
	@Override
	protected D getActualValue(ReferenceField<D> field) {
		return field.get();
	}

	/**
	 * @return the targetDomainObjectClass
	 */
	public Class<D> getTargetDomainObjectClass() {
		return this.targetDomainObjectClass;
	}

	
}
