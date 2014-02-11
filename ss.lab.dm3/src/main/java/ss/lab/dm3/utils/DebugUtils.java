package ss.lab.dm3.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Map;


public class DebugUtils {

	/**
	 * 
	 */
	public static final String DEBUG_PROTOCOL_NAME = "DEBUG_PROTOCOL";
	
	public static final String END_LINE = System.getProperty( "line.separator", "\r\n" );
	
	/**
	 * @return Returns caller stack tace as string
	 */
	public static String getCurrentStackTrace() {
		return getCurrentStackTrace(0); 
	}
		
	public static String getCurrentStackTrace( int size ) {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return stackTraceToString(3, size, stackTrace);
	}

	public static String stackTraceToString(final StackTraceElement[] stackTrace) {
		return stackTraceToString(0, 0, stackTrace);
	}
	/**
	 * @param stackTrace
	 * @return
	 */
	private static String stackTraceToString(int startElement, int size, final StackTraceElement[] stackTrace) {
		final StringBuilder sb = new StringBuilder();
		if (stackTrace != null) {
			final int end = size > 0 ? Math.min( startElement + size, stackTrace.length ) : stackTrace.length;			
			for (int n = startElement; n < end; ++n) {
				if ( sb.length() > 0 ) {
					sb.append( END_LINE );
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

	public static String dumpAllThreads() {
		return dumpAllThreads( "" );
	}
	/**
	 * @param string
	 * @return
	 */
	public static String dumpAllThreads(String name) {
		ThreadsStackTraceDumpBuilder dumpBuilder = new ThreadsStackTraceDumpBuilder( name );
		return dumpAllThreads(dumpBuilder);
	}

	/**
	 * @param dumpBuilder
	 * @return
	 */
	public static String dumpAllThreads(ThreadsStackTraceDumpBuilder dumpBuilder) {
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

	/**
	 * @return
	 */
	public static boolean isCallFrom() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void trace(String message, Object object) {
		if ( object instanceof Iterable ) {
			trace( message, (Iterable<?>) object ); 
		}
		else {
			if ( message != null ) {
				System.out.print( message );
				System.out.print( " " );
			}
			System.out.println( object );
		}	
	}
	
	public static void trace(Object object) {
		trace( null, object );
	}
	
	public static void trace(String message, Object ... objects) {
		for( Object obj : objects ) {
			trace( message, obj );
		}
	}
	
	public static void trace(Iterable<?> objects) {
		trace( null, objects );
	}
	
	public static void trace(String message, Iterable<?> objects) {
		if ( message != null ) {
			System.out.print( message );
			System.out.print( " " );
		}
		System.out.println( "{{" );
		for( Object obj : objects ) {
			System.out.println( obj );
		}
		System.out.println( "}}" );
	}

	/**
	 * @param string
	 * @param object
	 * @param string2
	 */
	public static void traceDetails(String message, Object object, String ... propertyNames ) {
		if ( object == null ) {
			trace( message, object );
		}
		else {
			if ( message != null ) {
				System.out.print( message );
				System.out.print( " " );
			}
			System.out.println( "{{" );
			final Class<?> clazz = object.getClass();
			for (String propertyName : propertyNames) {
				Method getter = ReflectionHelper.findGetter(clazz, propertyName);
				if ( getter != null ) {
					System.out.print( propertyName );
					System.out.print( " = "  );
					System.out.println( ReflectionHelper.invoke( object, getter ) );
				}				
			}
			System.out.println( "}}" );
		}
	}

	

	

}
 