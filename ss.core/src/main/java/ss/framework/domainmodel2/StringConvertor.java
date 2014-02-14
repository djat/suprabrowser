/**
 * 
 */
package ss.framework.domainmodel2;

/**
 * 
 */
public final class StringConvertor {

	public static long stringToLong( String value, long defaultValue ) {
		if ( value == null ) {
			return defaultValue;
		}
		else {
			try {
				return Long.parseLong(value);
			}
			catch(NumberFormatException ex ) {
				return defaultValue;
			}
		}
	}
	
	public static int stringToInt( String value, int defaultValue ) {
		if ( value == null ) {
			return defaultValue;
		}
		else {
			try {
				return Integer.parseInt(value);
			}
			catch(NumberFormatException ex ) {
				return defaultValue;
			}
		}
	}
	
	public static boolean stringToBoolean( String value, boolean defaultValue ) {
		if ( value == null ) {
			return defaultValue;
		}
		else {
			return Boolean.parseBoolean(value);			
		}
	}

	/**
	 * @param value
	 * @param i
	 * @return
	 */
	public static double stringToDouble(String value, int defaultValue) {
		if ( value == null ) {
			return defaultValue;
		}
		else {
			try {
				return Double.parseDouble(value);
			}
			catch(NumberFormatException ex ) {
				return defaultValue;
			}
		}
	}
	
	
}
