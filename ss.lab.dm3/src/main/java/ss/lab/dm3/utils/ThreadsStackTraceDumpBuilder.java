package ss.lab.dm3.utils;

import java.util.Date;

/**
 * 
 * @author Dmitry Goncharov
 */
public class ThreadsStackTraceDumpBuilder {

	private final StringBuilder sb = new StringBuilder();
	
	private final String name;

	private String endLine = DebugUtils.END_LINE;
	
	private int dumpedThreadCount = 0;
	/**
	 * @param name
	 */
	public ThreadsStackTraceDumpBuilder(final String name) {
		super();
		this.name = name;
	}

	public String getEndLine() {
		return this.endLine;
	}

	public void setEndLine(String endLine) {
		this.endLine = endLine;
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
		++ this.dumpedThreadCount; 
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
		print( this.endLine );
	}
	
	public int getDumpedThreadCount() {
		return this.dumpedThreadCount;
	}

	/**
	 * @return formatted threads stack traces
	 */
	@Override
	public String toString() {
		return this.sb.toString();
	}

}
