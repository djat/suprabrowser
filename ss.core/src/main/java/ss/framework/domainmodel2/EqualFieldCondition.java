/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public final class EqualFieldCondition extends FieldCondition {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EqualFieldCondition.class);
	
	private final Object expectedValue;
	
		/**
	 * @param descriptorClass
	 * @param expectedValue
	 */
	EqualFieldCondition(Class<? extends FieldDescriptor> descriptorClass, final Object expectedValue) {
		super(descriptorClass);
		this.expectedValue = expectedValue;
	}


	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.cache.AbstractFieldCondition#match(java.lang.Object)
	 */
	@Override
	protected boolean matchFieldValue(Object comparableFieldValue) {
		if ( logger.isDebugEnabled() ) {
			logger.debug( "compare " + this.getDescriptorClass().getSimpleName() + " (" + this.expectedValue + " ?= " + comparableFieldValue + ") " );
		}
		if ( comparableFieldValue instanceof ReferenceField ) {
			return ((ReferenceField)comparableFieldValue).equalsValue(this.expectedValue); 
		}
		else {
			if ( this.expectedValue == comparableFieldValue ) {
				return true;
			}
			if ( this.expectedValue == null ||
					comparableFieldValue == null ) {
				return false;
			}
			return this.expectedValue.equals(comparableFieldValue);
		}
	}

	/**
	 * @return the expectedValue
	 */
	public Object getExpectedValue() {
		return this.expectedValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescriptorClass().getSimpleName().toString() + " == " + this.expectedValue;
	}

	

}
