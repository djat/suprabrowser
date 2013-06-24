/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public class LongFieldDescriptor extends SimpleFieldDescriptor<LongField,Long> {

	/**
	 * @param baseDomainObjectClass
	 * @param name
	 */
	public LongFieldDescriptor(Class<? extends DomainObject> baseDomainObjectClass, String name) {
		super(baseDomainObjectClass, name, LongField.class);
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.FieldDescriptor#getActualValue(ss.framework.domainmodel2.Field)
	 */
	@Override
	protected Long getActualValue(LongField field) {
		return field.get();
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.fields.FieldDescriptor#getValueToSave(ss.framework.domainmodel2.fields.Field)
	 */
	@Override
	protected String getValueToSave(LongField field) {
		return String.valueOf( field.get() );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.fields.FieldDescriptor#setLoadedValue(ss.framework.domainmodel2.fields.Field, java.lang.String)
	 */
	@Override
	protected void setLoadedValue(LongField field, String strValue) {
		field.setSilently( StringConvertor.stringToLong(strValue, 0 ) );
	}

	
}
