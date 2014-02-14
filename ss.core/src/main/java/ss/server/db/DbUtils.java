/**
 * 
 */
package ss.server.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import ss.common.debug.DebugUtils;

/**
 * 
 */
public class DbUtils {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DbUtils.class);

	public static final String SUFFIX_FOR_MOST_USED_RECORDS = "  ORDER BY `modified` DESC, `moment` DESC, `create_ts` DESC";
	public static final String SUFFIX_FOR_TOP_RECORD = SUFFIX_FOR_MOST_USED_RECORDS + " LIMIT 1";

	private static final String STUB_ID = "-100000000";

	private static final char QUOTE = '\'';

	private static final char ESCAPE = '\\';

	/**
	 * @param sql
	 * @return
	 */
	public static String escapeQuotes(String unsafeValue) {
		final StringBuffer sb = new StringBuffer(unsafeValue.length());
		for (int i = 0; i < unsafeValue.length(); i++) {
			final char c = unsafeValue.charAt(i);
			if (c == QUOTE) {
				sb.append(QUOTE).append(QUOTE);
			} else if (c == ESCAPE) {
				sb.append(ESCAPE).append(ESCAPE);
			} else {
				sb.append(c);
			}
		}
		final String result = sb.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("Escaped string is: " + result);
		}
		return result;
	}

	/**
	 * Returns value with escaped quotes and enclosed in quotes
	 * 
	 * @param unsafeValue
	 * @return
	 */
	public static String quote(String unsafeValue) {
		return "'" + escapeQuotes(unsafeValue) + "'";
	}

	public static String formatNowDate() {
		final Date now = new Date();
		final SimpleDateFormat dateFormat = new SimpleDateFormat(
				XMLDB.YYYY_MM_DD_HH_MM_SS);
		return dateFormat.format(now);
	}

	/**
	 * @return
	 */
	public static String quotedStubId() {
		return quote(STUB_ID);
	}

	public static String xmlValueAttributeCondition(String attributeName,
			String attributeValue) {
		return xmlValueAttributeCondition(attributeName, attributeValue, true);
	}

	public static String xmlValueAttributeStaringCondition(String attributeName,
			String attributeValue) {
		return xmlValueAttributeCondition(attributeName, attributeValue, false);
	}

	public static String xmlValueAttributeCondition(String attributeName,
			String attributeValue, boolean exactValue) {
		if (attributeName.equals("value")) {
			logger.error("Illegal attribute name "
					+ DebugUtils.getCurrentStackTrace());
		}
		if (attributeValue == null) {
			return " LIKE '%%'";
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("  LIKE '%<");
			sb.append(attributeName);
			sb.append(" value=\"");
			sb.append(quoteXmlValue(attributeValue));
			if (exactValue) {
				sb.append("\"");
			}
			sb.append("%'");
			return sb.toString();
		}
	}

	public static String likeXmlInlineAttribute(String attributeName,
			String attributeValue) {
		if (attributeName.equals("value")) {
			logger.error("Illegal attribute name "
					+ DebugUtils.getCurrentStackTrace());
		}
		StringBuilder sb = new StringBuilder();
		sb.append("  LIKE '%");
		sb.append(attributeName);
		sb.append("=\"");
		sb.append(quoteXmlValue(attributeValue));
		sb.append("\"%'");
		return sb.toString();
	}

	/**
	 * TODO add mysql quote
	 * 
	 * @param attributeValue
	 * @return
	 */
	private static String quoteXmlValue(String attributeValue) {
		if (attributeValue == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int n = 0; n < attributeValue.length(); n++) {
			char ch = attributeValue.charAt(n);
			switch (ch) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("''");
				break;
			default:
				sb.append(ch);
				break;
			}
		}
		return sb.toString();
	}
}
