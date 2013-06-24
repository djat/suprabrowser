/**
 * 
 */
package ss.common;


/**
 *
 */
public class FormattedTimeSpan {

	private static final long SECOND_TICKS = 1000;
	
	private static final long MINUTE_TICKS = 60 * SECOND_TICKS;
	
	private static final long HOUR_TICKS = 60 * MINUTE_TICKS;
	
	private static final long DAY_TICKS = 24 * HOUR_TICKS;
	
	private static final long WEEK_TICKS = 7 * DAY_TICKS;
	
	private static final long MONTH_TICKS = 30 * DAY_TICKS;
	
	private static final long YEAR_TICKS = 365 * DAY_TICKS;
	
	private static final long [] DATE_TICKS = new long[] {
		YEAR_TICKS,
		MONTH_TICKS,
		WEEK_TICKS,
		DAY_TICKS,
		HOUR_TICKS,
		MINUTE_TICKS,
		SECOND_TICKS
	};
	
	private static final String [] DATE_TITLES = new String[] {
		"year", 
		"month", 
		"week", 
		"day",
		"hour", 
		"minute",
		"second"
	};
	
	private static final String [] MULTIPLE_SUFIX = new String[] {
		"s", 
		"es", 
		"s", 
		"s",
		"s",
		"s",
		"s"
	};
	
	private final int [] friendlyValues;
	
	private final boolean hasUnparsedTicks;
	
	public FormattedTimeSpan( final long ticks ) {
		this.friendlyValues = new int[ DATE_TICKS.length ];
		long reminder = ticks;
		for( int n = 0; n < this.friendlyValues.length; ++ n ) {
			this.friendlyValues[ n ] = (int)( reminder / (long)DATE_TICKS[ n ] );
			reminder -= this.friendlyValues[ n ] * DATE_TICKS[ n ];			
		}
		this.hasUnparsedTicks = reminder > 0;
	}
	
	private int [] getFriedlyValues() {
		return this.friendlyValues;
	}
	
	private int getFirstNonZeroValueIndex() {
		int [] values = getFriedlyValues();
		for (int n = 0; n < values.length; n++) {
			if ( values[ n ] > 0 ) {
				return n; 
			}
		}
		return values.length;
	}

	/**
	 * 
	 */
	public String toPrettyString() {
		final StringBuffer sb = new StringBuffer();
		final int [] values = getFriedlyValues();
		final int firstNonZeroValueIndex = getFirstNonZeroValueIndex();
		for (int n = 0; n < 2; n++) {
			final int valueIndex = n + firstNonZeroValueIndex;
			if ( valueIndex >= values.length ) {
				break;			
			}
			if ( sb.length() > 0 ) {
				sb.append( " " );
			}
			final int value = values[ valueIndex ];
			if(value==0) {
				continue;
			}
			sb.append( value );
			sb.append( " " );
			sb.append( DATE_TITLES[ valueIndex ] );
			if ( value > 1 ) {
				sb.append( MULTIPLE_SUFIX[ valueIndex ] );
			}			
		}
		if ( sb.length() > 0 ) { 
			return sb.toString();
		}
		else {
			if ( this.hasUnparsedTicks ) {
				return "less than " + DATE_TITLES[ DATE_TITLES.length - 1 ]; 
			}
			else {
				return "none";
			}
		}
	}
}

