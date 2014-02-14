package ss.common.formatting;

import java.util.Collection;
import java.util.List;

import ss.common.StringUtils;

public class ValuesStringBuilder {

	/**
	 * 
	 */
	private static final int VALUE_LENGTH_THRESHOLD = 80;

	/**
	 * 
	 */
	public static final String DEFAULT_VALUE_SEPARATOR = ", ";

	private final StringBuilder stringBuilder = new StringBuilder();
	
	private final String valueSeparator;
	
	private final boolean valueWrap;
	
	private boolean isFistValue = true;

	
	public ValuesStringBuilder() {
		this( DEFAULT_VALUE_SEPARATOR );
	}
	
	/**
	 * @param valueSeparator
	 */
	public ValuesStringBuilder(final String valueSeparator) {
		super();
		this.valueSeparator = valueSeparator != null ? valueSeparator : DEFAULT_VALUE_SEPARATOR;
		this.valueWrap = !valueSeparator.contains( StringUtils.getLineSeparator() );
	}

	/**
	 * @param object
	 */
	public void addValue(Object object) {
		appendValue( object );		
	}
	
	/**
	 * @param object
	 */
	public void addKeyAndValue(Object key, Object value ) {
		appendValue( key, "=", value );		
	}
	
	private void appendValue(Object ... compositeValue ) {
		if ( !this.isFistValue ) {
			appendValueSeparator();
		}
		for( Object value : compositeValue ) {
			final String strValue = value != null ? value.toString() : "[null]";
			this.stringBuilder.append( strValue );
			if ( this.valueWrap && strValue.length() > VALUE_LENGTH_THRESHOLD ) {
				this.stringBuilder.append( StringUtils.getLineSeparator() );
			}
		}		
		this.isFistValue = false;
	}

	/**
	 * @param list
	 * @param start
	 * @param size
	 */
	public void addValues(final List list, final int start, final int size) {
		final int topBound = size + start;
		for( int n = start; n < topBound; ++ n ) {
			addValue( list.get( n ) );
		}	
	}

	/**
	 * @param list
	 * @param start
	 * @param size
	 */
	public void addValues(final Collection list) {
		for( Object value : list ) {
			addValue( value );
		}	
	}
	/**
	 * @param string
	 */
	public void append(String string) {
		this.stringBuilder.append( string );
		
	}

	/**
	 * 
	 */
	public void appendValueSeparator() {
		this.stringBuilder.append( this.valueSeparator );		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.stringBuilder.toString();
	}
	
	
}
