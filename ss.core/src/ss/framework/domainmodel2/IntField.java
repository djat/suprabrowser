/**
 * 
 */
package ss.framework.domainmodel2;

/**
 * 
 */
public class IntField extends Field {
	
	private int value;
	
	/**
	 * @param descriptor
	 * @param objectOwner
	 */
	public IntField(FieldDescriptor descriptor, FieldMap fieldMap) {
		super(descriptor, fieldMap);
	}

	/**
	 * @return the value
	 */
	public int get() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void set(int value) {
		markDirty();
		setSilently( value );
	}
	
	/**
	 * @param value the value to set
	 */
	public void setSilently(int value) {
		this.value = value;
	}
	
}
