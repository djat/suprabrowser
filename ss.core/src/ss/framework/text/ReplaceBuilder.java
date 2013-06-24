package ss.framework.text;

import java.util.ArrayList;
import java.util.List;

public class ReplaceBuilder {

	private final List<Character> match = new ArrayList<Character>();
	
	private final List<String> replace = new ArrayList<String>();
	
	public ReplaceBuilder add( char ch, char value ) {
		return add( ch, String.valueOf( value ) );
	}
	
	public ReplaceBuilder add( char ch, String value ) {
		int existedIndex = this.match.indexOf( ch );
		if ( existedIndex >= 0 ) {
			this.match.remove( existedIndex );
			this.replace.remove(existedIndex );
		}
		this.match.add( ch );
		this.replace.add( value != null ? value : "" );		
		return this;
	}
	
	public IReplace getResult() {
		return new Replace( this.match, this.replace );
	}
}
