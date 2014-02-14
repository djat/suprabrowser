/**
 * 
 */
package ss.util;

import ss.client.ui.email.EmailAbstractShellCompositeUnit;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.StringUtils;
import ss.domainmodel.configuration.DomainProvider;

/**
 * @author zobo
 * 
 */
public class StringProcessor {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailAbstractShellCompositeUnit.class);

	private static String LAPKI = "\"";

	/**
	 * Private constructor
	 */
	private StringProcessor() {

	}

	/**
	 * Converts text to HTML like text. Replace chars < to &lt, > to &gt; etc.
	 * 
	 * @param text
	 *            String to be converted
	 * @return converted text.
	 */
	public static String toHTMLView(String text) {
		if ( text == null ) {
			return "";
		}
		String out = text.trim();
		out = out.replaceAll("&", "&amp;");
		out = out.replaceAll("<", "&lt;");
		out = out.replaceAll(">", "&gt;");
		out = out.replaceAll("\"", "&quot;");
		return out;
	}

	/**
	 * 
	 * @param text
	 *            String to be suited in Lapki
	 * @return
	 */
	public static String suitInLapki(String text) {
		if (text == null) {
			logger.warn("Null text is attemping to suit in lapki");
			return null;
		}
		String localText = text.trim();
		if (localText.startsWith(LAPKI)) {
			if (localText.endsWith(LAPKI)) {
				return localText;
			}
			localText += LAPKI;
			return localText;
		} else if (localText.endsWith(LAPKI)) {
			localText = LAPKI + localText;
			return localText;
		}
		return LAPKI + localText + LAPKI;
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String unsuitFromLapki(String text) {
		if (text == null) {
			logger.warn("Null text is attemping to be unsuited of lapki");
			return null;
		}
		String localText = text.trim();
		if (localText.equals(""))
			return localText;
		return localText.replaceAll(LAPKI, " ").trim();
	}

	/**
	 * @param recipientDesciption
	 * @return
	 */
	public static String removeToInEmail(String str) {
		String toRemove = "to:";
		String toReturn = str.trim();
		String strLower = toReturn.toLowerCase();
		if (strLower.startsWith(toRemove)) {
			toReturn = toReturn.substring(3).trim();
		}
		return toReturn;
	}
	
	public static String removeToAndRoutingNumberInEmails(final String str) {
		if (StringUtils.isBlank(str)){
			return "";
		}
		String toReturn = str;
		try {
			toReturn = removeToInEmail(str);
			final String domain = SpherePossibleEmailsSet.getDomainFromSingleAddress(str);
			if (DomainProvider.contains(domain)){
				return SpherePossibleEmailsSet.supplyAddressesWithRoutingNumber(toReturn, null, false);
			}
		} catch (Throwable ex){
			logger.error("Exception when trying getting pretty email address", ex);
		}
		return toReturn;
	}
}
