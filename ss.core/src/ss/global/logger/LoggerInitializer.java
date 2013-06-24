/**
 * 
 */
package ss.global.logger;


import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ss.common.EnumManager;
import ss.common.FileUtils;
import ss.common.ReflectionUtils;
import ss.common.debug.DebugUtils;
import ss.framework.errorreporting.EmbededAppenderInitializer;
import ss.framework.errorreporting.ILogEventTransport;
import ss.global.ILoggerInitializer;
import ss.global.LoggerUtils;
import ss.global.LoggerConfiguration;

/**
 *
 */
public class LoggerInitializer implements ILoggerInitializer {
	
	/**
	 * 
	 */
	private static final String LOG4J_PROPERTIES_FILE_NAME = "/log4j.properties";
	final static EnumManager INSTACE = new EnumManager();
	
	/* (non-Javadoc)
	 * @see ss.global.ILoggerProvider#init(ss.global.StartType)
	 */
	public synchronized void initialize(LoggerConfiguration type) {
    	if ( type == null  ) {
    		type = LoggerConfiguration.DEFAULT;
    	}
    	initialize( type.getConfigurationFileName(), type.getRootLoggerLevel(), type.getLogEventTransportClass(), type.isEmbededAppenderEnabled(), type != LoggerConfiguration.DEFAULT );
    }
    
    /* (non-Javadoc)
	 * @see ss.global.ILoggerProvider#init(java.lang.String)
	 */
    public synchronized void initialize(String configurationName) {
    	if ( configurationName == null ) {
    		initialize( LoggerConfiguration.DEFAULT );
    	}
    	else {
    		final LoggerConfiguration startType = INSTACE.parseValue( LoggerConfiguration.class, configurationName );
    		if ( startType != null ) {
    			initialize( startType );
    		}
    		else {
    			initialize( LoggerUtils.getConfigurationFileName( configurationName ), LoggerConfiguration.DEFAULT.getRootLoggerLevel(), LoggerConfiguration.DEFAULT.getLogEventTransportClass(), LoggerConfiguration.DEFAULT.isEmbededAppenderEnabled(), true );
    		}    		
    	}    	 
    }
        
    /* (non-Javadoc)
	 * @see ss.global.ILoggerProvider#init(java.lang.String, org.apache.log4j.Level, java.lang.Class, boolean)
	 */
    public synchronized void initialize(String confFileName, Level rootLoggerLevel, Class<? extends ILogEventTransport> logEventTransportClass, boolean embededAppenderEnable, boolean watchToCfgFileInNotFound ) {
    	confFileName = FileUtils.getCanonicalPath( confFileName );
    	final String description = "Cfg file name: " + confFileName + ", rootLoggerLevel " + rootLoggerLevel;
		System.out.println( "Begin SSLogger init. " + description );
    	Logger.getRootLogger().setLevel( rootLoggerLevel );
    	try {
    		if ( !FileUtils.isFileExist(confFileName ) ) {
    			System.err.println( "Logger configuration not found " + confFileName );
    		}
    		String message = "Logger initialized by ";
    		if ( watchToCfgFileInNotFound ) {
    			message += confFileName; 
    			PropertyConfigurator.configureAndWatch( confFileName );
    		}
    		else {
    			final InputStream log4jPropsStream = getClass().getResourceAsStream( LOG4J_PROPERTIES_FILE_NAME );
    			if ( log4jPropsStream != null ) {
    				message += LOG4J_PROPERTIES_FILE_NAME;
    				Properties props = new Properties();
    				props.load( log4jPropsStream );
        			PropertyConfigurator.configure( props );	
    			}
    			else {
    				message = "Logger is NOT initialized";
    			}
    		}
			System.out.println( message );
    		Logger.getRootLogger().info( message );
    	}
    	catch(Exception ex ) {
    		String message = "Failed to load configuration from " + confFileName;
    		System.err.print( message );
    		ex.printStackTrace( System.err );
			Logger.getRootLogger().error(message, ex);
    	}
    	if ( logEventTransportClass != null ) {
    		EmbededAppenderInitializer.INSTACE.initailize( logEventTransportClass.getCanonicalName(), embededAppenderEnable );
    	}
    	else {
    		Logger.getRootLogger().warn( "Log event transport class is NULL => EmbededAppender disabled." );
    	}
    }  
    
    
	/* (non-Javadoc)
	 * @see ss.global.ILoggerProvider#initByDefault()
	 */
	public void initializeByDefault() {
		if ( shouldInitializeByDefault() ) {
			System.out.println( "JUnit detected. Run initialize by default" );
			System.out.flush();
			initialize( LoggerConfiguration.DEFAULT );
		}
		else {
			System.err.println( "Please initialize SSLogger first. " + DebugUtils.getCurrentStackTrace() );
			System.err.println( "Initialize by default" );			
		}		
	}
	
	 /**
	 * @return
	 */
	private static boolean shouldInitializeByDefault() {
		return ReflectionUtils.isCalledByJUnit();
	}
    
}
