package ss.common;

import java.util.HashMap;
import java.util.Map;

import ss.common.enums.EnumValuesMap;

public class EnumManager {

	private final Map<Class, EnumValuesMap>  enumClassToValueMap = new HashMap<Class, EnumValuesMap >();
		
	public final static EnumManager SHARED_INSTANCE = new EnumManager();
	
	public <T extends Enum<T>> T parseValue( Class<T> enumType, String strValue ) {
		final EnumValuesMap<T> valuesMap = getEnumValueMap( enumType );
		return valuesMap.findValue( strValue  );
	}
	
	public <T extends Enum<T>> T getDefaultValue( Class<T> enumType) {
		return getEnumValueMap( enumType ).getDefaultValue();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> String getCanonicalStringValue( T value ) {
		if ( value == null ) {
			throw new ArgumentNullPointerException( "value" );
		}
		final Class<T> enumType = (Class<T>)value.getClass();
		final EnumValuesMap<T> valuesMap = getEnumValueMap( enumType );
		return valuesMap.getCanonicalStringValue( value );
	}
	
	@SuppressWarnings("unchecked")
	private synchronized <T extends Enum<T>> EnumValuesMap<T>  getEnumValueMap(Class<T> enumType) {
		EnumValuesMap<T> valuesMap = (EnumValuesMap<T>) this.enumClassToValueMap.get(enumType);
		if ( valuesMap == null ) {
			valuesMap = new EnumValuesMap<T>( enumType );
			this.enumClassToValueMap.put( enumType, valuesMap );
		}
		return valuesMap;
	}	
	
	
}
