/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public abstract class GenericFieldDescriptor<F extends GenericField<V>,V> extends SimpleFieldDescriptor<F, V>{

	/**
	 * @param baseDomainObjectClass
	 * @param name
	 * @param fieldClass
	 */
	public GenericFieldDescriptor(Class<? extends DomainObject> baseDomainObjectClass, String name, Class<F> fieldClass) {
		super(baseDomainObjectClass, name, fieldClass);
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.FieldDescriptor#getActualValue(ss.framework.domainmodel2.Field)
	 */
	@Override
	protected V getActualValue(F field) {
		return field.get();
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.FieldDescriptor#getValueToSave(ss.framework.domainmodel2.Field)
	 */
	@Override
	protected final String getValueToSave(F field) {
		return convertToString( field.get() );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.FieldDescriptor#setLoadedValue(ss.framework.domainmodel2.Field, java.lang.String)
	 */
	@Override
	protected final void setLoadedValue(F field, String strValue) {
		field.setSilently( convertFromString( strValue ) );
	}

	protected abstract String convertToString( V value );
	
	protected abstract V convertFromString( String value );


}
