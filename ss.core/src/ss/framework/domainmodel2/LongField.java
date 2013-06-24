/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public class LongField extends Field {
	
	private long value;
	
	/**
	 * @param descriptor
	 * @param objectOwner
	 */
	public LongField(FieldDescriptor descriptor, FieldMap fieldMap) {
		super(descriptor, fieldMap);
	}

	/**
	 * @return the value
	 */
	public long get() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void set(long value) {
		markDirty();
		setSilently( value );
	}
	
	/**
	 * @param value the value to set
	 */
	public void setSilently(long value) {
		this.value = value;
	}
	
}
