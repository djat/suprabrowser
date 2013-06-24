/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public class ReferenceField<D extends DomainObject> extends LazyField<D>{

	/**
	 * @param objectOwner
	 * @param descriptor
	 */
	public ReferenceField(ReferenceFieldDescriptor<D> descriptor,FieldMap fieldMap) {
		super(descriptor, fieldMap);
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.Field#getComparableValue()
	 */
	@Override
	public synchronized Object getComparableValue() {
		return this;
	}
	
	private synchronized long getActualId() {
		if ( isResolved() ) {
			return this.value != null ? this.value.getId() : ReferenceFieldDescriptor.NULL_REFERENCE_ID;
		}
		else {
			return StringConvertor.stringToLong(this.loadedStrValue, ReferenceFieldDescriptor.NULL_REFERENCE_ID );
		}
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.LazyField#equalsValue(java.lang.Object)
	 */
	public synchronized boolean equalsValue(Object expectedValue) {
		if ( expectedValue == null ) {
			return getActualId() == ReferenceFieldDescriptor.NULL_REFERENCE_ID;
		}
		else if ( expectedValue instanceof DomainObject ) {
			long id = ((DomainObject) expectedValue).getId();
			return ((ReferenceFieldDescriptor)this.descriptor).getTargetDomainObjectClass() == expectedValue.getClass() &&
				id == getActualId();
		}
		else {
			return false;
		}
	}
	
}

