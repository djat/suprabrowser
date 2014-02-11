package ss.lab.dm3.persist.transaction.concurrent;

import static d1.FastAccess.$;

import java.util.ArrayList;
import java.util.List;

public class RandomHelper {

	public static String randomName() {
		StringBuilder sb = new StringBuilder();
		int count = random( 5, 10 );
		for( int n = 0; n < count; ++ n ) {
			sb.append( (char) random( (int)'a', (int)'z' )  );			
		}
		return sb.toString();
	}

	public static Integer[] randomIntegers(int min, int max) {
		int maxRandom = max - min;
		Integer [] result = new Integer[ maxRandom + 1 ];
		for ( int n = min; n <= max; ++ n ) {
			for(;;) {
				int randomPos = random( 0, maxRandom );
				if ( result[ randomPos ] == null ) {
					result[ randomPos ] = new Integer(n);
					break;
				}
			}
		}
		return result;
	}

	public static int random(int min, int max) {
		return (int)( Math.random() * (max - min + 1)) + min;
	}

	public static <T> List<T> randomItems(List<T> items, int count) {
		if ( items.size() < count ) {
			throw new IllegalArgumentException( $("Can't get {0} items from {1}", count, items ) );
		}
		List<T> ret = new ArrayList<T>();
		for( Integer order : randomIntegers( 0, count - 1) ) {
			ret.add( items.get( order ) );
		}
		return ret;
	}

}
