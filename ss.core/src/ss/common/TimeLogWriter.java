/**
 * 
 */
package ss.common;


/**
 * @author zobo
 *
 */
public class TimeLogWriter {
	
	private static final String TIME_LOGGER_SUFFIX = "time";

	private final org.apache.log4j.Logger logger;
	
	private long originalTime;
	
	private final String className;
	
	private String baseTimeMessage;

	public TimeLogWriter(Class clazz){
		this(clazz, "");
	}
	
	public TimeLogWriter(Class clazz, String baseMessage){
		this.logger = ss.global.SSLogger.getLogger(clazz, TIME_LOGGER_SUFFIX);
		this.className = clazz.getSimpleName();
		this.baseTimeMessage = baseMessage;
		this.originalTime = System.currentTimeMillis();
		logWriterStarted();
	}
	
	private void logWriterStarted() {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Time writer created for " + this.className + 
					(StringUtils.isNotBlank(this.baseTimeMessage) ? (" - " + StringUtils.wrapInLapki(this.baseTimeMessage)) : ("")) );
		}
	}

	public void logTime( final String message ){
		logElapsed( message );
	}
	
	public void logTime(){
		logTime( "" );
	}
	
	public void logAndRefresh(final String message){
		logTime(message);
		refresh(message, false);
	}
	
	public void refresh(){
		this.originalTime = System.currentTimeMillis();
	}
	
	public void refresh(final String newBaseTimeMessage){
		refresh(newBaseTimeMessage, true);
	}
	
	public void refresh(final String newBaseTimeMessage, final boolean isLogWrite){
		refresh();
		this.baseTimeMessage = newBaseTimeMessage;
		if ((isLogWrite) && (this.logger.isDebugEnabled())) {
			this.logger.debug(getPrefixString() + " new message and time setted");
		}
	}
	
	private void logElapsed(final String message){
		final long elapsed = System.currentTimeMillis() - this.originalTime;
		final String prettyTime = getPrettyTime(elapsed);
		if (this.logger.isDebugEnabled()) {
			this.logger.debug(getPrefixString() + prettyTime + 
					(StringUtils.isNotBlank(message) ? (" to " + StringUtils.wrapInLapki(message)) : ("")));
		}
	}

	private String getPrettyTime(final long elapsedTime) {
		long elapsed = elapsedTime;
		String prettyTime = " min:sec:milisec - ";
		final long minutes = elapsed / 60000;
		prettyTime += minutes + ":";
		elapsed -=  minutes*60000;
		final long seconds = elapsed / 1000;
		prettyTime += seconds + ":";
		final long miliseconds = elapsed - seconds*1000; 
		prettyTime += miliseconds;
		return prettyTime;
	}
	
	private String getPrefixString(){
		String str = "Time - for " + this.className;
		if (StringUtils.isNotBlank(this.baseTimeMessage)){
			str += " - from " + StringUtils.wrapInLapki(this.baseTimeMessage);
		}
		str += ":";
		return str;
	}
}
