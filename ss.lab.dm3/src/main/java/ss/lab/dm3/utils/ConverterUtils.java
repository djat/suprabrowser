package ss.lab.dm3.utils;

import java.io.Serializable;

/**
 * @author Dmitry Goncharov
 *
 */
public class ConverterUtils {

	public static Boolean getBooleanValue(Serializable value) {
		return (Boolean) value;
	}
	
	public static <T extends Enum<?>> T getEnumValue(Class<T> enumClazz,Serializable value) {
		return null;
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static Long getLongValue(Serializable value) {
		if ( value == null ) {
			return null;
		}
		if ( value instanceof Long ) {
			return (Long) value;
		}
		if ( value instanceof Integer ) {
			return new Long( (Integer) value );  
		}
		if ( value instanceof String ) {
			return Long.parseLong( (String) value );
		}
		throw new IllegalArgumentException( "Can't get Long value from " + value );
	}

	/**
	 * @param value
	 * @return
	 */
	public static Integer getIntegerValue(Serializable value) {
		if ( value == null ) {
			return null;
		}
		if ( value instanceof Integer ) {
			return (Integer) value;
		}
		if ( value instanceof String ) {
			return Integer.parseInt( (String) value );
		}
		throw new IllegalArgumentException( "Can't get Integer value from " + value );
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static String getStringValue(Serializable value) {
		if ( value == null ) {
			return null;
		}
		if ( value instanceof String ) {
			return (String ) value;
		}
		return value.toString();
	}

	public static Double getDoubleValue(Serializable value) {
		if ( value == null ) {
			return null;
		}
		if ( value instanceof Double ) {
			return (Double) value;
		}
		if ( value instanceof String ) {
			return Double.parseDouble( (String) value );
		}
		throw new IllegalArgumentException( "Can't get Double value from " + value );
	}

	
}
