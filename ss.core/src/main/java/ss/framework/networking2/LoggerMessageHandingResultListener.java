/**
 * 
 */
package ss.framework.networking2;

import org.apache.log4j.Logger;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
public class LoggerMessageHandingResultListener implements IMessageHandlingResultListener {

	private final Logger logger;
	
	/**
	 * @param logger
	 */
	public LoggerMessageHandingResultListener(Logger logger) {
		super();
		if ( logger == null ) {
			throw new ArgumentNullPointerException( "logger" );
		}
		this.logger = logger;
	}


	/* (non-Javadoc)
	 * @see ss.common.networking2.ICommandHandingResultListener#error(ss.common.networking2.CommandHandlerRunner, ss.common.networking2.CommandHandleException)
	 */
	public void error(MessageHandlerRunnableAdaptor runner, CommandHandleException ex) {
		this.logger.error( "Handler failed " + runner,  ex );		
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.ICommandHandingResultListener#finished(ss.common.networking2.CommandHandlerRunner)
	 */
	public void finished(MessageHandlerRunnableAdaptor runner) {
		if ( this.logger.isDebugEnabled() ) {
			this.logger.debug( "Handler successfully finished " + runner );
		}		
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.ICommandHandingResultListener#unexpectedError(ss.common.networking2.CommandHandlerRunner, java.lang.RuntimeException)
	 */
	public void unexpectedError(MessageHandlerRunnableAdaptor runner, RuntimeException ex) {
		this.logger.fatal( "Handler failed " + runner,  ex );		
	}
}
