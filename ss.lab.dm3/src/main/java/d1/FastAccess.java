package d1;

import d1.utils.Utils;

public class FastAccess {

	public static String $( String format, Object ... args  ) {
		return Utils.format( format, args );
	}
}
