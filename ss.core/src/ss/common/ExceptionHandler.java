package ss.common;

/**
 * Standard class for exception handling
 */
public class ExceptionHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger.getLogger(ExceptionHandler.class);

	/**
	 * Singleton instance
	 */
	private static ExceptionHandler INSTANCE = new ExceptionHandler();
	
	/**
	 * Private constructor.
	 */
	private ExceptionHandler() {		
	}
	
	/**
	 * 
	 * Handle excpetion.
	 * In all cases it logs exception details.
	 * Show error dialog or don't show determins on the exception class and ExceptionHandler configuration. 
	 *  
	 * @param sender object that catch exception, if static context than sender 
	 * must be a class object  
	 * @param ex exception
	 */
	public static void handleException( Object sender, Throwable ex ) {
		INSTANCE.hanldeExceptionImpl(sender, ex);		
	}
	
	private void hanldeExceptionImpl( Object sender, Throwable ex ) {
//		TODO: full implementation
		String senderStr;
		try {
			senderStr = sender != null ? sender.toString() : "[null]";
		}
		catch( Throwable nullEx) {
			senderStr = "Cannot convert sender to string (" + nullEx.getMessage() + ")";
		}
		if ( ex != null ) {
			logger.error( "Exception occurs in " + senderStr, ex );
		}	
	}

	/**
	 * @param ex
	 */
	public static void handleException(Exception ex) {
		handleException( "Static context", ex);		
	}
}
