package ss.framework.entities;


public interface ISimpleEntityProperty extends IEntityProperty {

	/**
	 * Returns value
	 * 
	 * @return
	 */
	String getValue();
	
	/**
	 * Returns value or entity string ("") if no value found
	 * 
	 * @return
	 */
	String getValueOrEmpty();

	/**
	 * Sets value
	 * 
	 * @param value
	 */
	void setValue(String value);

	/**
	 * Sets the boolean value.
	 * In xml boolean values represeted as strings "true" and "false".
	 * By default (or parse erorr) value is false.
	 * 
	 * @param value 
	 */
	void setBooleanValue(boolean value);
	
	
	/**
	 * Gets the boolean value. 
	 * Returns true if getThreeStateBooleanValue() returns ThreeStateBoolean.True 
	 * otherwise false.
	 */
	boolean getBooleanValue();
	
	/**
	 * Returns true if property is defined in data
	 */
	boolean isValueDefined();

	/**
	 * @param value not null
	 */
	void setNotNullValue(String value);

	/**
	 * Calls getThreeStateBooleanValue 
	 * for ThreeStateBoolean.True returns true
	 * for ThreeStateBoolean.False returns false
	 * for ThreeStateBoolean.Default returns defaultValue 
	 */
	boolean getBooleanValue(boolean defaultValue);
	
	String getValueOrDefault(String defaultValue);
	
	/**
	 * 
	 * @param enumType enum class
	 * @param defaultValue value that will be returns if cannot parse enum string value 
	 * @return parsed enum value or defaultValue if have parse errors
	 */
	<T extends Enum<T>> T getEnumValue( Class<T> enumType, T defaultValue );
	
	/**
	 * @param enumType enum class 
	 * @return parsed enum value or default value if have parse errors
	 */
	<T extends Enum<T>> T getEnumValue( Class<T> enumType );
	
	/**
	 * @param enumType enum class
	 * @param strValue enum string value 
	 * @return parsed enum value or default value if have parse errors
	 */
	<T extends Enum<T>> void setEnumValue( T value );

	/**
	 * @return
	 */
	double getDoubleValue();

	/**
	 * @param value
	 */
	void setDoubleValue(double value);

	/**
	 * @return
	 */
	int getIntValue();

	/**
	 * @param value
	 */
	void setIntValue(int value);

	/**
	 * @return
	 */
	long getLongValue();
	
	void setLongValue(long value);
	
	void removeAllMatched();
}
