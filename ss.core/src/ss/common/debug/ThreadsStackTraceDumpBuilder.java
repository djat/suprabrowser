package ss.common.debug;
import java.util.Date;

import ss.common.StringUtils;


public class ThreadsStackTraceDumpBuilder {

	private final StringBuilder sb = new StringBuilder();
	
	private final String name;
	
	/**
	 * @param name
	 */
	public ThreadsStackTraceDumpBuilder(final String name) {
		super();
		this.name = name;
	}

	public void begin(int threadsCount) {
		print( "---" );
		print( this.name );
		print( " " );
		println( new Date() );
		print( threadsCount ); 
		println( " threads" );
		println( "{{{" );
	}
	
	public void add( Thread thread, StackTraceElement[] stackTrace ) {		
		print( "THREAD: " );
		println( thread.getName() );
		println( "Stack trace: " );
		println( DebugUtils.stackTraceToString(stackTrace) );
		println( "" );
	}

	public void end() {
		println( "}}}" );
		println( "" );
	}

	/**
	 * @param string
	 */
	private void print(Object obj ) {
		this.sb.append( obj );
	}



	/**
	 * @param string
	 */
	private void println(Object obj ) {
		print( obj );
		print( StringUtils.getLineSeparator() );
	}



	/**
	 * @return formatted threads stack traces
	 */
	public String toString() {
		return this.sb.toString();
	}

}
