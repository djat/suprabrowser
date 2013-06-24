package ss.common;

import java.util.ArrayList;
import java.util.Map;

import ss.common.formatting.ValuesStringBuilder;

public class MapUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MapUtils.class);
	
	public static boolean hasValue( Map map, String key, String expectedValue ) {
		if ( map == null ) {
			// throw new ArgumentNullPointerException( "map" );
			return false;
		}
		if ( key == null) {
			throw new ArgumentNullPointerException( "key" );
		}
		if ( expectedValue == null) {
			throw new ArgumentNullPointerException( "expectedValue" );
		}
		Object value = map.get( key );
		if ( value instanceof String &&
			value != null ) {
			return ((String)value).equalsIgnoreCase( expectedValue );
		}
		return false;		
	}

	/**
	 * @param map
	 * @param username
	 * @param sphere_id2
	 * @return
	 */
	public static String valuesWithStringKeysToString(final Map map, String ... keys ) {
		if ( map == null ) {
			return "[hastable is null]";
		}
		if ( keys == null ||
			 keys.length == 0 ) {
			ArrayList<String> keyList = getStringKeys(map);
			keys = new String[ keyList.size() ];
			keyList.toArray( keys );
		}
		final StringBuilder sb = new StringBuilder();
		sb.append( "size: " );
		sb.append( map.size() );
		sb.append( "{" );
		ValuesStringBuilder valuesStringBuilder = new ValuesStringBuilder(); 
		for( String key : keys ) {
			Object value = key != null ? map.get(key) : null;
			valuesStringBuilder.addKeyAndValue(key, value);
		}
		sb.append( valuesStringBuilder.toString() ); 
		sb.append( "}" );
		return sb.toString();
	}
	
	/**
	 * @param map
	 * @param username
	 * @param sphere_id2
	 * @return
	 */
	public static String allValuesToString(final Map map ) {
		if ( map == null ) {
			return "[hastable is null]";
		}
		final StringBuilder sb = new StringBuilder();
		sb.append( "count: " );
		sb.append( map.size() );
		sb.append( "{" );
		ValuesStringBuilder valuesStringBuilder = new ValuesStringBuilder(); 
		for( Object key : map.keySet() ) {
			Object value = key != null ? map.get(key) : null;
			valuesStringBuilder.addKeyAndValue(key, value);
		}
		sb.append( valuesStringBuilder.toString() ); 
		sb.append( "}" );
		return sb.toString();
	}
	
	/**
	 * @param map
	 * @return
	 */
	private static ArrayList<String> getStringKeys(final Map map) {
		ArrayList<String> keyList = new ArrayList<String>(); 
		for(Object keyObj : map.keySet() ) {
			if ( keyObj instanceof String ){
				keyList.add( (String)keyObj );
			}			
		}
		return keyList;
	}

	/**
	 * @param map
	 * @param key
	 * @return
	 */
	public static String requireValue(Map map, String key) {
		if ( map == null ) {
			throw new ArgumentNullPointerException( "map" );
		}
		final String value = (String)map.get(key);
		if ( value == null ) {
			throw new IllegalArgumentException( "Cannot find required value " + key + " in " + map );
		}
		return value;
	}

	/**
	 * @param session
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getValue(Map map, String key, String defaultValue) {
		if ( map == null ) {
			return null;
		}
		String value = (String) map.get(key);
		return value != null ? value : defaultValue;
	}
}
