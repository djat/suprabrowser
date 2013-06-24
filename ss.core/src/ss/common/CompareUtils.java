package ss.common;

public class CompareUtils {

	/**
	 * @param x object or null 
	 * @param y object or null
	 * @return true if x == y or x.equals( y )
	 */
	public static boolean equals( final Object x, final Object y ) {
		if ( x == y ) {
			return true;
		}
		if ( x == null ||
			 y == null ) {
			return false;
		}
		return x.equals( y );			
	}
}
