/**
 * 
 */
package ss.framework.domainmodel2;

import ss.common.ReflectionUtils;


/**
 *
 */
public abstract class FieldDescriptor<F extends Field,V> {
	
	private final Class<? extends DomainObject> baseDomainObjectClass;
	
	private final Class<F> fieldClass;
	
	private final String name;
	
	/**
	 * @param baseDomainObjectClass
	 * @param fieldClass
	 * @param name
	 */
	public FieldDescriptor(Class<? extends DomainObject> baseDomainObjectClass, final String name, Class<F> fieldClass) {
		super();
		this.baseDomainObjectClass = baseDomainObjectClass;
		this.fieldClass = fieldClass;
		this.name = name;
	}

	public final void save( F field, Record record ) {
		record.setText( this.name, getValueToSave(field)); 
	}
	
	public final void load( F field, Record record ) {
		setLoadedValue(field, record.getText(this.name ));	
	}
	
	public final V getFieldValue( F field ) {
		return getActualValue( field );
	}

	/**
	 * @param test
	 * @return
	 */
	public final F createField(FieldMap fieldMap ) {
		return ReflectionUtils.create(this.fieldClass, this, fieldMap );
	}
	
	protected abstract void setLoadedValue(F field, String strValue);

	protected abstract String getValueToSave(F field);
	
	/**
	 * @param field
	 */
	protected abstract V getActualValue(F field);
	
	public abstract Object getComparableValue(DomainObject domainObject);
	
	public synchronized final Object getActualValue(DomainObject domainObject) {
		return getActualValue( domainObject.getField( this ) );
	}
	
	/**
	 * @param domainObject
	 */
	public final void checkAcceptableObject(DomainObject domainObject) {
		if (!isAccepableObject(domainObject)) {
			throw new UnacceptableDomainObjectException( this.baseDomainObjectClass, domainObject );
		}
	}
	
	/**
	 * @param memberOwner
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final FieldCondition createEqualFieldCondition(V fieldValue) {
		return new EqualFieldCondition( (Class<FieldDescriptor>)getClass(), fieldValue );
	}

	/**
	 * @param object
	 * @return
	 */
	public final boolean isAccepableObject(DomainObject object) {
		return this.baseDomainObjectClass.isInstance(object);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.baseDomainObjectClass.getSimpleName() + "." + this.name;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @return the baseDomainObjectClass
	 */
	public Class<? extends DomainObject> getBaseDomainObjectClass() {
		return this.baseDomainObjectClass;
	}
	
	
	
}
