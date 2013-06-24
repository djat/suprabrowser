/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public abstract class LazyFieldDescriptor<F extends LazyField,V> extends FieldDescriptor<F,V> {

	/**
	 * @param domainObjectClass
	 * @param name
	 * @param fieldClass
	 */
	public LazyFieldDescriptor(Class<? extends DomainObject> domainObjectClass, String name, Class<F> fieldClass) {
		super(domainObjectClass, name, fieldClass);
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.fields.FieldDescriptor#setLoadedValue(ss.framework.domainmodel2.fields.Field, java.lang.String)
	 */
	@Override
	protected final void setLoadedValue(F field, String strValue) {
		field.setLoadedValue(strValue);
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.FieldDescriptor#getComparableValue(ss.framework.domainmodel2.DomainObject)
	 */
	@Override
	public synchronized final Object getComparableValue(DomainObject domainObject) {
		F field = domainObject.getField(this);
		return field.getComparableValue();
	}

	/**
	 * @param space 
	 * @param loadedStrValue
	 * @return
	 */
	public abstract V resolve(AbstractDomainSpace space, String loadedStrValue);

}
