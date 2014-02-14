package ss.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ss.common.formatting.ListPartInformation;
import ss.common.formatting.ValuesStringBuilder;

public class ListUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ListUtils.class);
	/**
	 * @param members
	 * @return
	 */
	public static String valuesToString(final List list) {
		return valuesToString(list, formatFirstAndLastListValues(list) );
	}
	
	/**
	 * @param members
	 * @return
	 */
	public static String valuesToString(Object ... values) {
		final List<Object> list = toList(values);
		return valuesToString(list, formatFirstAndLastListValues(list) );
	}

	/**
	 * @param list
	 * @param formattedValues
	 * @return
	 */
	private static String valuesToString(Collection values, final ValuesStringBuilder formattedValues ) {
		if ( values == null ) {
			return "[collection is null]";
		}		
		StringBuilder sb = new StringBuilder();
		sb.append( "size: " );
		sb.append( values.size() );
		sb.append( " {" );
		sb.append( formattedValues.toString() );
		sb.append( "}" );
		return sb.toString();
	}
	
	public static String allValuesToString(Collection list) {
		return allValuesToString( list, ValuesStringBuilder.DEFAULT_VALUE_SEPARATOR );
	}
	
	/**
	 * @param name
	 * @param lineSeparator
	 */
	public static String allValuesToString(Collection list, String valueSeparator ) {
		if ( list != null ) {
			final ValuesStringBuilder formattedValues = new ValuesStringBuilder( valueSeparator );
			formattedValues.addValues(list);
			return valuesToString(list, formattedValues );
		}
		else {
			return valuesToString( null, null );
		}
	}
	
	private static ValuesStringBuilder formatFirstAndLastListValues(final List list) {
		if ( list == null ) {
			return null;
		}
		final int size = list.size();
		final ListPartInformation firstPart = new ListPartInformation( size, 0 );
		final int lastPartStart = Math.max( size - ListPartInformation.MAX_PART_SIZE, firstPart.getEnd() + 1 );
		final ListPartInformation lastPart = new ListPartInformation( size, lastPartStart );
		if (logger.isDebugEnabled()) {
			logger.debug( "For " + size + " have parts: " + firstPart + " and " + lastPart);
		}
		final ValuesStringBuilder formattedValues = new ValuesStringBuilder();
		formattedValues.addValues( list, firstPart.getStart(), firstPart.getSize() );
		if ( lastPart.getStart() >= firstPart.getEnd() && lastPart.getSize() > 0 ) {
//			if ( lastPart.getStart() == firstPart.getSize() && 
//					lastPart.getStart() > 0 ) {
//				formattedValues.appendValueSeparator();
//			}
			if ( lastPart.getStart() > firstPart.getSize() ) {
				formattedValues.appendValueSeparator();
				formattedValues.append( "..(" + (lastPart.getStart() - firstPart.getEnd() - 1) + ").." );	
			}		
			formattedValues.addValues( list, lastPart.getStart(), lastPart.getSize() );
		}
		return formattedValues;
	}

	/**
	 * @param ancestorsSystemsNames
	 * @param itemSphereCoreId
	 * @return
	 */
	public static boolean containsByEqual(List<String> items, String expectedItem ) {
		if ( items == null ||
			 expectedItem == null ) {
			return false;
		}
		for( String item : items ) {
			if ( CompareUtils.equals( item, expectedItem ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param records
	 * @return
	 */
	public static <T> T getFirst(List<T> items) {
		if ( items == null || items.size() == 0 ) {
			return null;
		}
		return items.get( 0 );
	}

	/**
	 * @param bytes
	 * @return
	 */
	public static String arrayToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		sb.append( "Total size: " + bytes.length );
		sb.append( ". First " );
		final int sizeToOut = Math.min( bytes.length, 80 );
		sb.append( sizeToOut );
		sb.append( " bytes: " );
		for (int i = 0; i < sizeToOut; i++) {
			sb.append( bytes[i] );
		}
		return sb.toString();
	}

	/**
	 * @param genericSuperclass
	 * @return
	 */
	public static <T> String allValuesToString(T [] array) {
		if ( array == null ) {
			return "[null]";
		}
		ArrayList<T> collection = new ArrayList<T>( array.length );
		for( T item : array ) {
			collection.add( item );
		}
		return allValuesToString(collection);
	}

	/**
	 * @param handlers
	 * @return
	 */
	public static <T> List<T> toList(Iterable<T> iterable) {
		if ( iterable == null ) {
			return null;
		}
		final ArrayList<T> list = new ArrayList<T>();		
		for( T item : iterable ) {
			list.add( item );
		}
		return list;
	}
	
	public static <T> List<T> toList( T ... values ) {
		if ( values == null ) {
			return null;
		}
		List<T> list = new ArrayList<T>( values.length );
		for( T value : values ) {
			list.add( value );
		}
		return list;
	}

	public static <T> List<T> getIntersection( List<T> list1, List<T> list2 ){
		if ( (list1 == null) || (list2 == null) ) {
			throw new NullPointerException("list1 or list2 is null");
		}
		final List<T> result = new ArrayList<T>();
		for ( T t : list1 ) {
			if ( (t != null) && (list2.contains(t)) ) {
				result.add( t );
			}
		}
		return result;
	}
}
