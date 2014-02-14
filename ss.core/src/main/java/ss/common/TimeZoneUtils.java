/**
 * 
 */
package ss.common;

/**
 * @author roman
 *
 */

import java.util.ArrayList;
import java.util.List;

public class TimeZoneUtils {

	public static final String UTC = "UTC";
	private static final String UTC$1 = "UTC+1";
	private static final String UTC$2 = "UTC+2";
	private static final String UTC$3 = "UTC+3";
	private static final String UTC$3_30 = "UTC+3:30";
	private static final String UTC$4 = "UTC+4";
	private static final String UTC$4_30 = "UTC+4:30";
	private static final String UTC$5 = "UTC+5";
	private static final String UTC$5_45 = "UTC+5:45";
	private static final String UTC$6 = "UTC+6";
	private static final String UTC$6_30 = "UTC+6:30";
	private static final String UTC$7 = "UTC+7";
	private static final String UTC$8 = "UTC+8";
	private static final String UTC$8_45 = "UTC+8:45";
	private static final String UTC$9 = "UTC+9";
	private static final String UTC$9_30 = "UTC+9:30";
	private static final String UTC$10 = "UTC+10";
	private static final String UTC$10_30 = "UTC+10:30";
	private static final String UTC$11 = "UTC+11";
	private static final String UTC$11_30 = "UTC+11:30";
	private static final String UTC$12 = "UTC+12";
	private static final String UTC$12_45 = "UTC+12:45";
	private static final String UTC$13 = "UTC+13";
	private static final String UTC$14 = "UTC+14";
	private static final String UTC_1 = "UTC-1";
	private static final String UTC_2 = "UTC-2";
	private static final String UTC_3 = "UTC-3";
	private static final String UTC_3_30 = "UTC-3:30";
	private static final String UTC_4 = "UTC-4";
	private static final String UTC_4_30 = "UTC-4:30";
	private static final String UTC_5 = "UTC-5";
	private static final String UTC_6 = "UTC-6";
	private static final String UTC_7 = "UTC-7";
	private static final String UTC_8 = "UTC-8";
	private static final String UTC_9 = "UTC-9";
	private static final String UTC_9_30 = "UTC-9:30";
	private static final String UTC_10 = "UTC-10";
	private static final String UTC_11 = "UTC-11";
	private static final String UTC_12 = "UTC-12";

	private final List<String> zones = new ArrayList<String>();

	public static final TimeZoneUtils INSTANCE = new TimeZoneUtils();

	private TimeZoneUtils() {
		this.zones.add(UTC_12);
		this.zones.add(UTC_11);
		this.zones.add(UTC_10);
		this.zones.add(UTC_9_30);
		this.zones.add(UTC_9);
		this.zones.add(UTC_8);
		this.zones.add(UTC_7);
		this.zones.add(UTC_6);
		this.zones.add(UTC_5);
		this.zones.add(UTC_4_30);
		this.zones.add(UTC_4);
		this.zones.add(UTC_3_30);
		this.zones.add(UTC_3);
		this.zones.add(UTC_2);
		this.zones.add(UTC_1);
		this.zones.add(UTC);
		this.zones.add(UTC$1);
		this.zones.add(UTC$2);
		this.zones.add(UTC$3);
		this.zones.add(UTC$3_30);
		this.zones.add(UTC$4);
		this.zones.add(UTC$4_30);
		this.zones.add(UTC$5);
		this.zones.add(UTC$5_45);
		this.zones.add(UTC$6);
		this.zones.add(UTC$6_30);
		this.zones.add(UTC$7);
		this.zones.add(UTC$8);
		this.zones.add(UTC$8_45);
		this.zones.add(UTC$9);
		this.zones.add(UTC$9_30);
		this.zones.add(UTC$10);
		this.zones.add(UTC$10_30);
		this.zones.add(UTC$11);
		this.zones.add(UTC$11_30);
		this.zones.add(UTC$12);
		this.zones.add(UTC$12_45);
		this.zones.add(UTC$13);
		this.zones.add(UTC$14);
	}

	public List<String> getZoneList() {
		return this.zones;
	}
}
