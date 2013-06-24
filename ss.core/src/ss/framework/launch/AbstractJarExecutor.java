/**
 * 
 */
package ss.framework.launch;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;

import ss.framework.launch.MessageClassifier.MessageType;

/**
 *
 */
public abstract class AbstractJarExecutor {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractJarExecutor.class);
	
	private static org.apache.log4j.Logger antMesssageLogger = ss.global.SSLogger
			.getLogger( AbstractJarExecutor.class, "ant" );
	
	private final MessageClassifier messageClassifier = new MessageClassifier();
	
	private volatile CantLaunchJarException jarRunException = null;

	/**
	 * 
	 */
	public AbstractJarExecutor() {
		super();
	}

	/**
	 * @throws CantLaunchJarException
	 */
	protected final void throwLaunchExceptionIfHas() throws CantLaunchJarException {
		final CantLaunchJarException exception = this.getJarRunException();
		if (exception != null) {
			throw exception;
		}
	}

	/**
	 * @param message
	 */
	protected void processMessage(String message) {
		message = "{ANT} " + message; 
		final MessageType type = this.messageClassifier.classify(message);
		if (type == MessageType.FATAL) {
			processFatalMessage(message);
		} else if (type == MessageType.ERROR) {
			processErrorMessage(message);
		} else if ( antMesssageLogger.isInfoEnabled() ) {
			antMesssageLogger.info( message );
		}
	}

	/**
	 * @param message
	 */
	protected void processErrorMessage(String message) {
		antMesssageLogger.warn( message );
	}

	/**
	 * @param message
	 */
	protected void processFatalMessage(String message) {
		antMesssageLogger.error(message);
		this.setJarRunException(new CantLaunchJarException(message));		
	}

	/**
	 * @param jarRunException
	 *            the jarRunException to set
	 */
	protected synchronized void setJarRunException(CantLaunchJarException jarRunException) {
		this.jarRunException = jarRunException;
	}

	/**
	 * @return the jarRunException
	 */
	protected synchronized CantLaunchJarException getJarRunException() {
		return this.jarRunException;
	}


	/**
	 * @return
	 */
	protected final BuildLogger createBuildLogger() {
		return new RunListener();
	}
	
	public abstract void runUntilRelease(final Project project, final String targetName) throws CantLaunchJarException;
		

	class RunListener extends DefaultLogger {

		/**
		 * 
		 */
		public RunListener() {
			super();
			setOutputPrintStream(System.out);
			setErrorPrintStream(System.err);
			setMessageOutputLevel(Project.MSG_INFO);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.tools.ant.DefaultLogger#messageLogged(org.apache.tools.ant.BuildEvent)
		 */
		@Override
		public void messageLogged(BuildEvent event) {
			super.messageLogged(event);
			String message = event.getMessage();
			processMessage(message != null ? message : "");
		}

	}
}