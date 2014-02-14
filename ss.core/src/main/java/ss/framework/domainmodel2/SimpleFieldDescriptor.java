/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public abstract class SimpleFieldDescriptor<F extends Field,V> extends FieldDescriptor<F,V> {

	/**
	 * @param baseDomainObjectClass
	 * @param name
	 * @param fieldClass
	 */
	public SimpleFieldDescriptor(Class<? extends DomainObject> baseDomainObjectClass, String name, Class<F> fieldClass) {
		super(baseDomainObjectClass, name, fieldClass);		
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.FieldDescriptor#getComparableValue(ss.framework.domainmodel2.DomainObject)
	 */
	@Override
	public final Object getComparableValue(DomainObject domainObject) {
		return getActualValue(domainObject);
	}
	
	

}
