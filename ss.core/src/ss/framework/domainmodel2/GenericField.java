/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class GenericField<T> extends Field {

	protected T value = null;
	
	/**
	 * @param descriptor
	 * @param domainObjectOwner
	 */
	public GenericField(GenericFieldDescriptor descriptor, FieldMap fieldMap) {
		super(descriptor, fieldMap);
	}
	/**
	 * @return the value
	 */
	public final T get() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public final void set(T value) {
		markDirty();
		setSilently(value);
	}
	
	/**
	 * @param value the value to set
	 */
	public final void setSilently(T value) {
		this.value = value;
	}

}