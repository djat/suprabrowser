/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class IntFieldDescriptor extends SimpleFieldDescriptor<IntField,Integer> {

	/**
	 * @param baseDomainObjectClass
	 * @param name
	 */
	public IntFieldDescriptor(Class<? extends DomainObject> baseDomainObjectClass, String name) {
		super(baseDomainObjectClass, name, IntField.class);
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.FieldDescriptor#getActualValue(ss.framework.domainmodel2.Field)
	 */
	@Override
	protected Integer getActualValue(IntField field) {
		return field.get();
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.fields.FieldDescriptor#getValueToSave(ss.framework.domainmodel2.fields.Field)
	 */
	@Override
	protected String getValueToSave(IntField field) {
		return String.valueOf( field.get() );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.fields.FieldDescriptor#setLoadedValue(ss.framework.domainmodel2.fields.Field, java.lang.String)
	 */
	@Override
	protected void setLoadedValue(IntField field, String strValue) {
		field.setSilently( StringConvertor.stringToInt(strValue, 0 ) );
	}

	
}
