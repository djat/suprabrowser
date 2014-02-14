package ss.common.enums;

import java.util.HashMap;
import java.util.Map;

import ss.common.ArgumentNullPointerException;

public class EnumValuesMap<T extends Enum<T>> {

	private final Class<T> enumClass;
	
	
	private final Map<String,T> lowerCaseNameToValue = new HashMap<String,T>();
	
	private final T defaultValue;
	
	public EnumValuesMap( Class<T> enumClass ) {
		this.enumClass = enumClass;
		initializeItems();
		this.defaultValue = findDefaultValue(); 		 
	}

	private void initializeItems() {
		T[] universe = this.enumClass.getEnumConstants();
		if (universe == null) {
			throw new IllegalArgumentException(
					this.enumClass.getName() + " is not an enum type");
		}		
		if ( universe.length == 0 ) {
			throw new IllegalArgumentException( "Cannot work with empty enum " + this.enumClass );			
		}
		for (T constant : universe) {
			this.lowerCaseNameToValue.put( getCanonicalStringValue( constant ), constant );			
		}
	}
	
	/**
	 * @return default enum value
	 */
	private T findDefaultValue() {
		T defaultValue = null;
		for( T value : this.lowerCaseNameToValue.values() ) {
			if ( defaultValue == null ||
				 value.ordinal() < defaultValue.ordinal() ) {
				defaultValue = value;
			}
		}
		if ( defaultValue == null ) {
			throw new IllegalStateException( "Cannot find default value" );
		}
		return defaultValue;
	}
	

	/**
	 * @return the defaultValue
	 */
	public T getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * @return the enumClass
	 */
	public Class getEnumClass() {
		return this.enumClass;
	}

	/**
	 * @param strValue
	 * @return 
	 */
	public T findValue(String strValue) {
		if ( strValue == null ) {
			return null;
		}
		return this.lowerCaseNameToValue.get( strValue.toLowerCase() );
	}
	
	/**
	 * Retruns canonical name for value 
	 * @param value
	 * @return
	 */
	public String getCanonicalStringValue( T value ) {
		if ( value == null ) {
			throw new ArgumentNullPointerException( "value" );
		}
		final String name = ((Enum)value).name();
		return name.toLowerCase();
	}
}
