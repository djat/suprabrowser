/**
 * 
 */
package ss.global;

import ss.framework.errorreporting.EmbededAppender;
import ss.framework.errorreporting.EmbededAppenderInitializer;

/**
 *
 */
public class LoggerUtils {

	private static final String LOGGER_BASE_NAME = "logger.conf";

	public synchronized static void enableEmbededAppender() {
    	final EmbededAppender embededAppender = EmbededAppenderInitializer.INSTACE.getEmbededAppender();
    	if ( embededAppender != null ) {
    		embededAppender.setEnabled( true );
    	}
    }
	
	/**
	 * @return
	 */
	static String getConfigurationFileName() {
		return getConfigurationFileName(null);
	}

	/**
	 * @param string
	 * @return
	 */
	public static String getConfigurationFileName(String subName) {
		if (subName == null || subName.length() == 0) {
			return LOGGER_BASE_NAME;
		}
		return LOGGER_BASE_NAME + "." + subName;
	}
}
