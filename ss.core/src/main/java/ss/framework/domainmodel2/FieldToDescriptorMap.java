/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.Hashtable;

/**
 *
 */
final class FieldToDescriptorMap {

	/**
	 * Singleton instance
	 */
	public final static FieldToDescriptorMap INSTANCE = new FieldToDescriptorMap();

	private final Hashtable<Class<? extends Field>, Class<? extends FieldDescriptor>> fieldClassToDescriptor = new Hashtable<Class<? extends Field>, Class<? extends FieldDescriptor>>();  
	
	private FieldToDescriptorMap() {
		add( LongField.class, LongFieldDescriptor.class );
		add( StringField.class, StringFieldDescriptor.class );
		add( IntField.class, IntFieldDescriptor.class );
	}

	private <F extends Field> void add( Class<F> fieldClass,Class<? extends FieldDescriptor<F,?>> descriptorClass ) {
		this.fieldClassToDescriptor.put(fieldClass, descriptorClass);
	}
	
	@SuppressWarnings("unchecked")
	public <F extends Field> Class<FieldDescriptor<F,?>> get(Class<F> fieldClass )  {
		return (Class)this.fieldClassToDescriptor.get(fieldClass);
	}
	
}
