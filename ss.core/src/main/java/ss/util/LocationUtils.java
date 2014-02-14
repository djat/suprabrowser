/**
 * 
 */
package ss.util;

import java.io.File;

/**
 * 
 */
public class LocationUtils {

	/**
	 * 
	 */
	private static final String LAST_LOGIN_XML_FILE_NAME = "last_login.xml";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LocationUtils.class);

	public static File getLastLoginFile() {
		return VariousUtils.getSupraFile(LAST_LOGIN_XML_FILE_NAME);
	}
}
