package ss.framework.errorreporting;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


import ss.common.ArgumentNullPointerException;
import ss.global.SSLogger;

public final class EmbededAppenderInitializer {

	/**
	 * Please use getLogger  
	 */	
	private static org.apache.log4j.Logger logger;
	
	public final static EmbededAppenderInitializer INSTACE = new EmbededAppenderInitializer();

	private static final String EMBEDED_APPENDER_NAME = "remote-appender";

	private EmbededAppenderInitializer() {		
	}
		
	public synchronized void initailize( String eventTransportClassName, boolean enabled ) {
		if ( eventTransportClassName  == null ) {
			throw new ArgumentNullPointerException( "eventTransportClassName" );
		}
		final Logger rootLogger = Logger.getRootLogger();
		if ( rootLogger != null ) {
			Level level = rootLogger.getLevel();
			if ( level.isGreaterOrEqual( Level.ERROR ) ) {
				rootLogger.setLevel( Level.ERROR );
			}
			Appender embededAppender = rootLogger.getAppender( EMBEDED_APPENDER_NAME );
			if ( embededAppender == null ) {
				embededAppender = new EmbededAppender( eventTransportClassName, enabled );
				embededAppender.setName( EMBEDED_APPENDER_NAME );
				rootLogger.addAppender( embededAppender );
			}
		}		
	}
	
	public synchronized EmbededAppender getEmbededAppender() {
		final Logger rootLogger = Logger.getRootLogger();
		if ( rootLogger != null ) {
			Appender embededAppender = rootLogger.getAppender( EMBEDED_APPENDER_NAME );
			if ( embededAppender instanceof EmbededAppender ) {
				return (EmbededAppender) embededAppender;
			}
		}
		return null;
	}

	/**
	 * @return the logger
	 */
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger getLogger() {
		if ( logger == null ) {
			logger = SSLogger.getLogger( EmbededAppenderInitializer.class );
		}
		return logger;
	}
	
}
