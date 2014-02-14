/**
 * 
 */
package ss.framework.domainmodel2;

/**
*
*/
public abstract class Field {
	
	private final FieldMap fieldMapOwner;
	
	protected final FieldDescriptor descriptor; 

	/**
	 * @param domainObjectOwner
	 * @param name
	 */
	public Field(FieldDescriptor descriptor, FieldMap fieldMapOwner ) {
		super();
		this.descriptor = descriptor;
		descriptor.checkAcceptableObject( fieldMapOwner.getObjectOwner() );
		this.fieldMapOwner = fieldMapOwner;
	}

	protected final void markDirty() {
		this.fieldMapOwner.markDirty();
	}
	
	protected final AbstractDomainSpace getSpaceOwner() {
		return this.fieldMapOwner.getSpaceOwner();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.descriptor.toString();
	}
	
	
}
