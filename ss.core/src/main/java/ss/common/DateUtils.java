/**
 * 
 */
package ss.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.Statement;
import ss.server.db.XMLDB;
import ss.util.DateTimeParser;

/**
 * TODO rename : dateToCanonicalString to dateToDbString
 */
public class DateUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DateUtils.class);
	
	public final static Locale CANONICAL_LOCALE = Locale.US;	
	
	private final static DateFormat CANONICAL_DATE_FORMAT = new SimpleDateFormat(XMLDB.YYYY_MM_DD_HH_MM_SS, CANONICAL_LOCALE );

	public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone( "GMT" );
	
	/**
	 * @return
	 */
	public static String formatNowCanonicalDate() {
		return dateToCanonicalString( new Date() );
	}
	
	public static String dateToCanonicalString( final Date date ) {
		return CANONICAL_DATE_FORMAT.format(date);
	}
	
	public static Date canonicalStringToDate( final String stringDate ) {
		try {
			Date date = new Date();
			date.setTime(Long.parseLong(stringDate));
			return  date;
		} catch (Exception ex) {
			try {
				return CANONICAL_DATE_FORMAT.parse(stringDate);
			} catch (ParseException ex1) {
				ExceptionHandler.handleException(DateUtils.class, ex );
				logger.error( "Cannot parse date " + stringDate + ", returing January 1, 1970, 00:00:00 GMT.", ex );
				return new Date();// January 1, 1970, 00:00:00 GMT
			}
		}
	}

	/**
	 * @param idleTimeTicks
	 * @return
	 */
	public static String timeSpanToPrettyString(long ticks) {
		final FormattedTimeSpan formattedTimeSpan = new FormattedTimeSpan( ticks );
		return formattedTimeSpan.toPrettyString();
	}

	/**
	 * @param startTime
	 * @return
	 */
	public static String dateToCanonicalString(long ticks ) {
		return dateToCanonicalString( new Date( ticks ) );
	}
	
	
	/**
	 * @param current
	 * @return
	 */
	public static String dateToXmlEntityString(Date current) {
		return DateFormat.getTimeInstance(DateFormat.LONG).format( current ) + " "
			+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);
	}

	
	public static boolean isMessageWrittenToday(Statement statement, Date currentDate) {
		int ticksInDay = 86400000;
		int amendment = 10800000;
		
		Date serverDate = new Date();
		serverDate.setTime(currentDate.getTime()+SupraSphereFrame.INSTANCE.getTimeDifference());
		
		if (logger.isDebugEnabled()) {
			logger.debug("current : "+serverDate+"  in ticks : "+(serverDate.getTime()+amendment));
		}
		Date statementDate = DateTimeParser.INSTANCE.parseToDate(statement.getMoment());
		if (logger.isDebugEnabled()) {
			logger.debug("current : "+statementDate+"  in ticks : "+(statementDate.getTime()+amendment));
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("current d: "+(statementDate.getTime()+amendment)/ticksInDay+"  state d : "+(serverDate.getTime()+amendment)/ticksInDay);
		}
		return (statementDate.getTime()+amendment)/ticksInDay == (serverDate.getTime()+amendment)/ticksInDay;
	}
	
}
