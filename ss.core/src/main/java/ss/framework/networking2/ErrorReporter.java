/**
 * 
 */
package ss.framework.networking2;

import ss.common.DateUtils;
import ss.common.StringUtils;
import ss.common.debug.DebugUtils;

/**
 *
 */
final class ErrorReporter {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ErrorReporter.class);
	
	public enum Level {
		ERROR,
		UNEXPECTED_ERROR
	}
	
	/**
	 * Singleton instance
	 */
	public final static ErrorReporter INSTANCE = new ErrorReporter();

	private int totalErrorsCount = 0;
	
	private ErrorReporter() {
	}
	
	public synchronized String report(Command command, Exception ex, Level level ) {
		int errorNumber = ++ this.totalErrorsCount;
		final String reportForServer = getReportForServer(errorNumber, command, ex );
		if ( level == Level.ERROR ) {
			logger.error( reportForServer );
		}
		else {
			logger.fatal( reportForServer );
		}
		return getReportForClient(errorNumber, command, ex );
	}

	/**
	 * @param command
	 * @param ex
	 * @param errorNumber
	 * @return
	 */
	private static String getReportForClient(int errorNumber, Command command, Exception ex) {
		String messageDetails;
		if ( ex != null ) {
			messageDetails = ex.getClass().getName() + ", message: " + ex.getMessage();
		}
		else {
			messageDetails = "no exception info";
		}
		return getReport(errorNumber, command, messageDetails);
	}

	/**
	 * @param command
	 * @param ex
	 * @param errorNumber
	 * @param level 
	 * @return
	 */
	private static String getReportForServer(int errorNumber, Command command, Exception ex ) {
		String messageDetails = DebugUtils.getExceptionInfo( ex ); 
		return getReport( errorNumber, command, messageDetails );
	}
	
	/**
	 * @param command
	 * @param errorNumber
	 * @param messageDetails
	 * @return
	 */
	private static String getReport(int errorNumber, Command command, String messageDetails) {
		StringBuilder sb = new StringBuilder();
		sb.append( "Command failed " );
		sb.append( command.getClass().getName() );
		sb.append( " [" );
		sb.append( command.getSendId() );
		sb.append( "]" );
		sb.append( StringUtils.getLineSeparator() );
		sb.append( "Error number E#" );
		sb.append( errorNumber );
		sb.append( ", " );
		sb.append( DateUtils.formatNowCanonicalDate() );
		sb.append( StringUtils.getLineSeparator() );
		sb.append( "Details: " );		
		sb.append( messageDetails != null ? messageDetails : "[NULL]" );
		return sb.toString();
	}
	
	
}
