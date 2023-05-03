/**
 * 
 */
package ss.util;

import java.text.DateFormat;
import java.util.TimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author roman
 * 
 */
public class DateTimeParser {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DateTimeParser.class);

	public final static DateTimeParser INSTANCE = new DateTimeParser();

	private DateTimeParser() {
	}

	public Date parseToDate(String dateString) {
		if (dateString != null && dateString.length() > 0) {
			String pattern = "hh:mm:ss a z MMM dd, yyyy";
			// 11:38:30 AM IST 29-Apr-2023
			DateFormat df = new SimpleDateFormat(pattern, Locale.US);
			try {
				return df.parse(dateString);
			} catch (ParseException ex) {
				try {
					pattern = "hh:mm:ss z dd.MM.yyyy";
					df = new SimpleDateFormat(pattern, Locale.getDefault());
					return df.parse(dateString);
				} catch (ParseException ex1) {
					try {
						pattern = "yyyy-MM-dd hh:mm:ss";
						df = new SimpleDateFormat(pattern, Locale.getDefault());
						return df.parse(dateString);						
					} catch (ParseException ex2) {
						try {
							pattern = "hh:mm:ss a z dd-MMM-yyyy";
							df = new SimpleDateFormat(pattern, Locale.ENGLISH);
					        df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
							return df.parse(dateString);
						} catch (ParseException ex3) {
							ex3.printStackTrace();
							logger
							.error("Can't parse this string to either english or russian date : "
									+ dateString);
						}
					}
				}
			}
		} else {
			logger.error("Empty string to parse Date");
		}
		return Calendar.getInstance().getTime();
	}
}
