/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public abstract class FieldCondition {

	private final Class<? extends FieldDescriptor> descriptorClass;
	
	/**
	 * @param selector
	 */
	public FieldCondition(Class<? extends FieldDescriptor> descriptorClass) {
		super();
		this.descriptorClass = descriptorClass;
	}
	
	/**
	 * @param object
	 * @return
	 */
	public boolean match(DomainObject object) {
		FieldDescriptor descriptor = DescriptorManager.INSTANCE.get(this.descriptorClass);
		if ( descriptor.isAccepableObject(object) ) {
			return matchFieldValue( descriptor.getComparableValue( object ) );
		}
		else {
			return false;
		}		
	}


	/**
	 * @param comparableFieldValue
	 */
	protected abstract boolean matchFieldValue(Object comparableFieldValue);


	/**
	 * @return the selector
	 */
	public final Class<? extends FieldDescriptor> getDescriptorClass() {
		return this.descriptorClass;
	}

	 
}
