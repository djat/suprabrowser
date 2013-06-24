package ss.common.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import ss.common.StringUtils;

public class DebugUtils {

	/**
	 * 
	 */
	public static final String DEBUG_PROTOCOL_NAME = "DEBUG_PROTOCOL";
	
	/**
	 * @return Returns caller stack tace as string
	 */
	public static String getCurrentStackTrace() {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return stackTraceToString(3, stackTrace);
	}

	public static String stackTraceToString(final StackTraceElement[] stackTrace) {
		return stackTraceToString(0, stackTrace);
	}
	/**
	 * @param stackTrace
	 * @return
	 */
	private static String stackTraceToString(int startElement, final StackTraceElement[] stackTrace) {
		final StringBuilder sb = new StringBuilder();
		if (stackTrace != null) {
			for (int n = startElement; n < stackTrace.length; ++n) {
				if ( sb.length() > 0 ) {
					sb.append( StringUtils.getLineSeparator() );
				}
				sb.append("\tat ");
				sb.append(stackTrace[n]);				
			}
		}
		return sb.toString();
	}

	/**
	 * @param ex
	 * @return
	 */
	public static String getExceptionInfo(Throwable ex) {
		if ( ex == null ) {
			return null;
		}
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter( writer ); 
		ex.printStackTrace( printWriter );
		printWriter.flush();
		return writer.toString();
	}

	/**
	 * @param string
	 * @return
	 */
	public static String dumpAllThreads(String name) {
		ThreadsStackTraceDumpBuilder dumpBuilder = new ThreadsStackTraceDumpBuilder( name );
		Map<Thread, StackTraceElement[]> allStackTrace = Thread.getAllStackTraces();
		dumpBuilder.begin( allStackTrace.size() );
		for( Thread thread : allStackTrace.keySet() ) {
			StackTraceElement[] stackTrace = allStackTrace.get( thread );
			if ( thread != Thread.currentThread() ) {
				dumpBuilder.add(thread, stackTrace);
			}
		}		
		dumpBuilder.end();
		return dumpBuilder.toString();
	}

	/**
	 * @param ex
	 * @return
	 */
	public static String toSignificantMessage(Throwable ex) {
		if ( ex == null ) {
			return null;
		}
		Throwable withNotEmptyMessage = findFirstWithNotEmptyMessage(ex);
		if ( withNotEmptyMessage != null ) {
			return withNotEmptyMessage.getMessage();				
		}
		else {
			return ex.getClass().getName();
		}	
	}
	
	public static Throwable findFirstWithNotEmptyMessage(Throwable ex) {
		while( ex != null && ( ex.getMessage() == null || ex.getMessage().length() == 0 ) ) {
			ex = ex.getCause();
		}
		return ex;
	}

	

}
 