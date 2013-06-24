/**
 * 
 */
package ss.server.errorreporting;

import org.apache.log4j.spi.LoggingEvent;

import ss.framework.errorreporting.CantCreateSessionException;
import ss.framework.errorreporting.ICreateSessionInformation;
import ss.framework.errorreporting.ILogEventTransport;
import ss.framework.errorreporting.ILogStorage;
import ss.framework.errorreporting.SessionInformation;
import ss.framework.errorreporting.network.LogEvent;

/**
 *
 */
public class ServerLogEventTransport implements ILogEventTransport {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ServerLogEventTransport.class);
	
	private boolean initialized = false;
	
	private SessionInformation sessionInformation;

	private final ILogStorage logStorage = DbLogStorage.INSTACE;;
	
	/* (non-Javadoc)
	 * @see ss.framework.errorreporting.ILogEventTransport#close()
	 */
	public void close() {
		//NOOP
	}

	/* (non-Javadoc)
	 * @see ss.framework.errorreporting.ILogEventTransport#send(org.apache.log4j.spi.LoggingEvent)
	 */
	public synchronized void send(final LoggingEvent loggingEvent) {
		if ( !this.initialized ) {
			this.initialized = true;
			try {
				this.sessionInformation = this.logStorage.createSession( new ICreateSessionInformation() {
					public String getContext() {
						return null;
					}
					public String getSessionKey() {
						return null;
					}
					public String getUserName() {
						return "SERVER";
					}
				});
			} catch (CantCreateSessionException ex) {
				logger.error( "TODO message here",  ex );
			}
		}
		if ( this.sessionInformation != null ) {
			this.logStorage.store( new LogEvent( this.sessionInformation.getId(), null, loggingEvent ) );
		}		
	}

}
