/**
 * 
 */
package ss.framework.errorreporting.network;

import org.apache.log4j.spi.LoggingEvent;

import ss.client.networking2.ClientProtocolManager;
import ss.common.IdentityUtils;
import ss.framework.errorreporting.ILogEventTransport;
import ss.framework.errorreporting.SessionInformation;
import ss.framework.networking2.CommandExecuteException;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.ProtocolLifetimeAdapter;
import ss.framework.networking2.ProtocolLifetimeEvent;
import ss.framework.threads.CantInitializeException;
import ss.framework.threads.LazyActionDispatcher;

/**
 * 
 */
public abstract class AbstractNetworkLogEventTransport implements ILogEventTransport {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractNetworkLogEventTransport.class);


	private final LogActionDispatcher logActionDispatcher = new LogActionDispatcher();
	
	public final void send(LoggingEvent loggingEvent) {
		this.logActionDispatcher.dispatch(loggingEvent);
	}

	public final void close() {
		this.logActionDispatcher.beginUninitialize();
	}

	protected abstract CreateProtocolResult createProtocol() throws CantInitializeException;

	
	private class LogActionDispatcher extends
			LazyActionDispatcher<LoggingEvent> {

		private volatile SessionInformation sessionInformation;

		private volatile Protocol protocol;

		/**
		 * @param name
		 */
		public LogActionDispatcher() {
			super(IdentityUtils.getNextRuntimeId("EventTransport"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see errorreporting.threads.LazyActionDispatcher#dispatchImmediately(java.lang.Object)
		 */
		@Override
		protected void dispatchImmediately(LoggingEvent action) {
			LogEvent event = new LogEvent(this.sessionInformation.getId(),
					null, action );
			event.fireAndForget(this.protocol);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see errorreporting.threads.LazyActionDispatcher#intitializing()
		 */
		@Override
		protected void intitializing() throws CantInitializeException {
			boolean success = false;
			try {
				if (logger.isDebugEnabled()) {
					logger.debug( "Initializing " + this );
				}
				CreateProtocolResult result = createProtocol(); 
				this.protocol = result.getProtocol();
				this.protocol.addProtocolListener( new ProtocolLifetimeAdapter() {

					/* (non-Javadoc)
					 * @see ss.framework.networking2.ProtocolLifetimeAdapter#beginClose(ss.framework.networking2.ProtocolLifetimeEvent)
					 */
					@Override
					public void beginClose(ProtocolLifetimeEvent e) {
						super.beginClose(e);
						beginUninitialize();
					}
				});
				if (logger.isDebugEnabled()) {
					logger.debug( "Starting protocol " );
				}
				this.protocol.start( ClientProtocolManager.INSTANCE );

				try {
					if (logger.isDebugEnabled()) {
						logger.debug( "Received session info " + this.sessionInformation );
					}
					final InitializeCommand initializeCommand = new InitializeCommand( result.getSessionKey(), result.getUserLogin(), null );
					this.sessionInformation = initializeCommand.execute(this.protocol,
							SessionInformation.class);
					if ( this.sessionInformation == null ) {
						throw new CantInitializeException( "Session information is null" ); 
					}
					success = true;
				} catch (CommandExecuteException ex) {
					throw new CantInitializeException(ex);
				}	
			}
			finally {
				if ( !success ) {
					logger.warn( "Initialization failed " + this );
					this.sessionInformation = null;
					if ( this.protocol != null ) {
						this.protocol.beginClose();
						this.protocol = null;
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see errorreporting.threads.LazyActionDispatcher#uninitializing()
		 */
		@Override
		protected void uninitializing() {
			if (this.protocol != null) {
				this.protocol.beginClose();
			} else {
				logger.error("Illegal state. Protocol is null for " + this);
			}
			this.protocol = null;
			this.sessionInformation = null;
		}
	}




}