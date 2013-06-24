package ss.global;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class SSLogger {

	/**
	 * 
	 */
	private static final String LOGGER_PROVIDER_CLASS_NAME = "ss.global.logger.LoggerInitializer";

	private static boolean UNDER_INITIALIZATION = false;
	
	private static boolean FIRST_GET_LOGGER = false; 

	private static ILoggerInitializer LOGGER_PROVIDER = null;

	private SSLogger() {
	}

	/**
	 * 
	 */
	private static void endInitialization() {
		UNDER_INITIALIZATION = false;
	}

	/**
	 * 
	 */
	private static void beginInitialation() {
		UNDER_INITIALIZATION = true;
	}

	/**
	 * 
	 */
	private static void createLoggerProvider() {
		try {
			Class<?> clazz = Class.forName(LOGGER_PROVIDER_CLASS_NAME);
			LOGGER_PROVIDER = (ILoggerInitializer) clazz.newInstance();
		} catch (Exception ex) {
			System.err.println("Can't create logger provider " + ex);
		}
	}

	/**
	 * 
	 */
	private static void initializeByDefault() {
		createLoggerProvider();
		LOGGER_PROVIDER.initializeByDefault();
	}

	public synchronized static void initialize(LoggerConfiguration type) {
		beginInitialation();
		try {
			createLoggerProvider();
			LOGGER_PROVIDER.initialize(type);
		} finally {
			endInitialization();
		}
	}
	
	public synchronized static void skipInitialization() {
		beginInitialation();
		try {
			createLoggerProvider();
		} finally {
			endInitialization();
		}
	}

	public synchronized static void initialize(String configurationName) {
		beginInitialation();
		try {
			createLoggerProvider();
			LOGGER_PROVIDER.initialize(configurationName);
		} finally {
			endInitialization();
		}
	}

	public synchronized static Logger getLogger(Class<?> clientClass, String sufix) {
		return getLogger(clientClass.getName() + "." + sufix);
	}

	public synchronized static Logger getLogger(Class<?> clientClass) {
		return getLogger(clientClass.getName());
	}

	private synchronized static Logger getLogger(String name) {
		if (!isInitialized()) {
			if (UNDER_INITIALIZATION) {
				if ( FIRST_GET_LOGGER ) {
					System.out.println( "Configure via BasicConfigurator" );
					System.out.flush();
					BasicConfigurator.configure();
				}
			} else {
				initializeByDefault();
			}
		}
		return Logger.getLogger(name);
	}

	

	/**
	 * @return the initialized
	 */
	private static synchronized boolean isInitialized() {
		return LOGGER_PROVIDER != null;
	}
}
