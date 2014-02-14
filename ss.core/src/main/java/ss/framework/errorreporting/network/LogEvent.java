/**
 * 
 */
package ss.framework.errorreporting.network;

import org.apache.log4j.spi.LoggingEvent;

import ss.common.StringUtils;
import ss.framework.errorreporting.ILogEvent;
import ss.framework.networking2.Event;

/**
 *
 */
public final class LogEvent extends Event implements ILogEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1704760061060798360L;

	private final long sessionId;
		
	private final String level;
	
	private final String threadName;
	
	private final String locationInformation;
	
	private final String message;
	
	private final String[] stackTrace;
	
	private final String context;
	
	public LogEvent( long sessionId, String context, LoggingEvent loggingEvent ) {
		this.sessionId = sessionId;
		this.level = loggingEvent.getLevel().toString();
		this.threadName = loggingEvent.getThreadName();
		this.locationInformation = loggingEvent.getLocationInformation().fullInfo;
		final Object message = loggingEvent.getMessage();
		this.message = message != null ? message.toString() : "[null]";
		this.stackTrace = loggingEvent.getThrowableStrRep();
		this.context = context;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return this.level;
	}

	/**
	 * @return the locationInformation
	 */
	public String getLocationInformation() {
		return this.locationInformation;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @return the sessionId
	 */
	public long getSessionId() {
		return this.sessionId;
	}

	/**
	 * @return the stackTrace
	 */
	public String getStackTrace() {
		if ( this.stackTrace == null ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for( String line : this.stackTrace ) {
			if ( sb.length() > 0 ) {
				sb.append( StringUtils.getLineSeparator() );
			}
			sb.append( line );
		}
		return sb.toString();
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return this.threadName;
	}

	/**
	 * @return the context
	 */
	public String getContext() {
		return this.context;
	}

	
	
}
