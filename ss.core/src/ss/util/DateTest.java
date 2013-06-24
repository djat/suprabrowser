/*
 * Created on Oct 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import ss.global.SSLogger;

public class DateTest {
	
	private static final Logger logger = SSLogger.getLogger(DateTest.class);

	public static void main(String[] args) {

		try {

			String date = "5:52:09 PM EDT Oct 10, 2005";
			SimpleDateFormat df = new SimpleDateFormat("h:m:s a z MMM dd, yyyy");

			Date d = df.parse(date);

			Date current = new Date();
			System.out.println("d " + d.getTime());
			System.out.println("D: " + d.toString());
			System.out.println("C: " + current.toString());

			long endMoment = current.getTime() - d.getTime();

			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.sql.Timestamp ts = new java.sql.Timestamp(endMoment);

			String endMomentString = df2.format(ts);
			System.out.println("ENd moment: " + endMomentString);

		} catch (ParseException pe) {

			logger.error(pe.getMessage(), pe);
		}
	}

}
