/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class StringFieldDescriptor extends GenericFieldDescriptor<StringField, String>{

	/**
	 * @param baseDomainObjectClass
	 * @param name
	 * @param fieldClass
	 */
	public StringFieldDescriptor(Class<? extends DomainObject> baseDomainObjectClass, String name ) {
		super(baseDomainObjectClass, name, StringField.class );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.GenericFieldDescriptor#convertFromString(java.lang.String)
	 */
	@Override
	protected String convertFromString(String value) {
		return value;
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.GenericFieldDescriptor#convertToString(java.lang.Object)
	 */
	@Override
	protected String convertToString(String value) {
		return value;
	}

}
