/**
 * 
 */
package ss.server.admin.gui;

import ss.global.LoggerConfiguration;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class SupraServerCreatorGUI {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger;
	
	public static void main(String[] args) {
		SSLogger.initialize(LoggerConfiguration.DEFAULT);
		logger = ss.global.SSLogger.getLogger(SupraServerCreatorGUI.class);
		final SupraServerCreateFrame frame = new SupraServerCreateFrame();
		frame.setBlockOnOpen(true);
		frame.open();
	}
}
