package ss.framework.entities.xmlentities;

import ss.common.ArgumentNullPointerException;
import ss.common.EnumManager;
import ss.framework.domainmodel2.StringConvertor;
import ss.framework.entities.AbstractEntityProperty;
import ss.framework.entities.ISimpleEntityProperty;

public abstract class AbstractSimpleEntityProperty extends AbstractEntityProperty implements ISimpleEntityProperty {

	private final static EnumManager ENUM_MANAGER = new EnumManager();
	
	/* (non-Javadoc)
	 * @see entities.ISimpleEntityProperty#getSurname()
	 */
	public abstract String getValue();
	
	/* (non-Javadoc)
	 * @see entities.ISimpleEntityProperty#setSurname(java.lang.String)
	 */
	public abstract void setValue(String value);
	
	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#hasValue()
	 */
	public abstract boolean isValueDefined();
	
	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#getValueIfNoValueFound()
	 */
	public final String getValueOrEmpty() {
		final String value = getValue();
		return value != null ? value : "";
	}
	
	public final String getValueOrDefault(String defaultValue) {
		if(isValueDefined()) {
			String value = getValue();
			if(value != null) {
				return value;
			}
		}
		return defaultValue;
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#getBooleanValue(boolean)
	 */
	public final boolean getBooleanValue() {
		return getBooleanValue( false );		
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#setBooleanValue(boolean)
	 */
	public final void setBooleanValue(boolean value) {
		setEnumValue( value ? ThreeStateBoolean.True : ThreeStateBoolean.False );
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#setNotEmptyValue(java.lang.String)
	 */
	public final void setNotNullValue(final String value) {
		if ( value == null ) {
			throw new ArgumentNullPointerException( "value");
		}
		setValue(value);
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#getBooleanValue(boolean)
	 */
	public final boolean getBooleanValue(boolean defaultValue) {
		final ThreeStateBoolean gettedValue = getEnumValue( ThreeStateBoolean.class );
		if ( ThreeStateBoolean.Default == gettedValue ) {
			return defaultValue; 
		}
		return gettedValue == ThreeStateBoolean.True;
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#getEnumValue(java.lang.Class, java.lang.Enum)
	 */
	public final <T extends Enum<T>> T getEnumValue( Class<T> enumType, T defaultValue ) {
		T parsedValue = ENUM_MANAGER.parseValue( enumType, getValue() );
		return parsedValue != null ? parsedValue : defaultValue;
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#getEnumValue(java.lang.Class)
	 */
	public final <T extends Enum<T>> T getEnumValue( Class<T> enumType ) {
		return getEnumValue( enumType, ENUM_MANAGER.getDefaultValue( enumType ) );
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#setEnumValue(java.lang.Enum)
	 */
	public final <T extends Enum<T>> void setEnumValue( T value ) {
		this.setValue( ENUM_MANAGER.getCanonicalStringValue( value ) );
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#getDoubleValue()
	 */
	public double getDoubleValue() {
		return StringConvertor.stringToDouble( getValue(), 0 );
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#setDoubleValue(double)
	 */
	public void setDoubleValue(double value) {
		setValue( String.valueOf( value ) );
		
	}

	public int getIntValue() {
		return StringConvertor.stringToInt(getValue(), 0 );
	}

	public void setIntValue(int value) {
		setValue( String.valueOf(value) );		
	}
	
	
	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#getBooleanValue(boolean)
	 */
	public final long getLongValue() {
		return StringConvertor.stringToLong(getValue(), 0 );		
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#setBooleanValue(boolean)
	 */
	public final void setLongValue(long value) {
		setValue( String.valueOf(value) );
	}
	
	
}