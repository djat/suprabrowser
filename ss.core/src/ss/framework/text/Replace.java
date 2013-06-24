package ss.framework.text;

import java.util.List;

final class Replace implements IReplace {

	private final char[] match;
	
	private final String[] replace;
	
	/**
	 * @param match
	 * @param replace
	 */
	public Replace(final List<Character> match, final List<String> replace) {
		super();
		if ( match.size() != replace.size() ) {
			throw new IllegalArgumentException( "Match and replace has different sizes " + match.size() + ", " + replace.size() );
		}		
		this.match = new char[ match.size() ];
		for( int n = 0; n < this.match.length; ++ n ) {
			this.match[ n ] = match.get( n );
		}
		this.replace = replace.toArray( new String[ replace.size() ] );
	}

	public String replace( String value ) {
		if ( value == null || value.length() == 0 ) {
			return value;
		}
		final StringBuilder sb = new StringBuilder();
		for( int n = 0; n < value.length(); ++ n ) {
			final char ch = value.charAt( n );
			int replaceIndex = getReplaceIndex( ch );
			if ( replaceIndex >= 0 ) {
				sb.append( this.replace[replaceIndex] );	
			}
			else {
				sb.append( ch );
			}
		}
		return sb.toString();
		
	
	}

	/**
	 * @param ch
	 * @return
	 */
	private int getReplaceIndex(char ch) {
		for( int n = 0; n < this.match.length; ++ n  ) {
			if ( this.match[ n ] == ch ) {
				return n;
			}
		}
		return -1;
	}

	
}
