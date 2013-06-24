package ss.global;

import org.apache.log4j.Level;

import ss.client.errorreporting.ClientLogEventTransport;
import ss.framework.errorreporting.ILogEventTransport;
import ss.server.errorreporting.ServerLogEventTransport;

public enum LoggerConfiguration {
	
	DEFAULT( "", Level.WARN ), 
    CLIENT( "client", Level.WARN, ClientLogEventTransport.class, false ),
    SMTP( "smtp", Level.WARN, ClientLogEventTransport.class, true  ),
    SERVER( "server", Level.WARN, ServerLogEventTransport.class, true  );    
      
    private final String configurationBaseName;
    
    private final Level level;
    
    private final Class<? extends ILogEventTransport> logEventTransportClass;
    
    private final boolean embededAppenderEnabled;
	
    private LoggerConfiguration(String configurationFileName, Level level ) {
    	this( configurationFileName, level, null, false );
    }
	/**
	 * @param configurationFileName
	 * @param level
	 */
	private LoggerConfiguration(String configurationBaseName, Level level, Class<? extends ILogEventTransport> logEventTransport, boolean embededAppenderEnabled ) {		
		this.configurationBaseName = configurationBaseName;
		this.level = level;
		this.logEventTransportClass = logEventTransport;
		this.embededAppenderEnabled = embededAppenderEnabled; 
	}

	public String getConfigurationFileName() {
		return LoggerUtils.getConfigurationFileName( this.configurationBaseName );
	}
	
	public Level getRootLoggerLevel() {
		return this.level; 
	}

	/**
	 * @return the logEventTransport
	 */
	public Class<? extends ILogEventTransport> getLogEventTransportClass() {
		return this.logEventTransportClass;
	}
	/**
	 * @return the embededAppenderEnabled
	 */
	public boolean isEmbededAppenderEnabled() {
		return this.embededAppenderEnabled;
	}
	
	
	

}
