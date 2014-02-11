package d1.utils;

public class Utils {

	public static String format( String format, Object ... args ) {
		if ( format == null ) {
			return null;
		}
		int length = format.length();
		StringBuilder sb = new StringBuilder( length );
		boolean escape = false;
		for ( int pos = 0; pos < length; ++ pos ) {
			char ch = format.charAt( pos );
			if ( escape ) {
				escape = false;
				sb.append( ch );
			}
			else {
				if ( ch == '{' ) {
					pos ++;
					pos = processArgument(format, sb, pos, args);
				}
				else if ( ch == '\\' ) { 
					escape = true;
				}
				else {
					sb.append( ch );
				}
			}
		}
		return sb.toString();
	}

	private static int processArgument(String format, StringBuilder sb,
			int pos, Object... args) {
		int length = format.length();
		char ch;
		int index = -1;
		String argToAppend = "Can't find close brace";
		boolean skip = false;
		boolean error = false;
		while( pos < length ) {
			ch = format.charAt( pos );
			if ( ch == '}' ) {
				if ( index == -1 ) {
					error = true;
					argToAppend = "Invalid index at #" + pos;
				} 
				else if ( index > args.length ) {
					error = true;
					argToAppend = "Argument index " + index + " is out of bounds (" + args.length + ")";  
				}
				else {
					argToAppend = String.valueOf( args[ index ] );
				}
				break;
			}
			if ( !skip ) {
				int digit = (int)ch - (int)'0';
				if ( digit < 0 || digit > 9 ) {
					error = true;
					skip = true;
					argToAppend = "Invalid digit at #" + pos;
					break;
				}
				index = index == -1 ? digit : index * 10 + digit;
			}
			++ pos;							
		}
		if ( error ) {
			sb.append( "[" );
		}
		sb.append( argToAppend );
		if ( error ) {
			sb.append( "]" );
		}
		return pos;
	}

}
