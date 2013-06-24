/**
 * 
 */
package ss.framework.networking2.keepalive;

import ss.common.DateUtils;
import ss.common.operations.AbstractLoopOperation;
import ss.common.operations.OperationBreakException;
import ss.framework.networking2.Protocol;

/**
 *
 */
public class KeepAlivePingSender {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(KeepAlivePingSender.class);
	
	/**
	 * 
	 */
	private static final int PING_SLEEP_TIME = 60000; // 1 min
	
	private final Protocol protocol;
	
	private final SenderOperation senderOperation;  
	/**
	 * @param protocol
	 */
	public KeepAlivePingSender(final Protocol protocol) {
		super();
		this.protocol = protocol;
		this.senderOperation = new SenderOperation();
	}

	/**
	 * 
	 */
	public synchronized void start() {
		if ( this.senderOperation.isNeverBeenUsed()	) {
			this.senderOperation.start();
		}
	}

	/**
	 * 
	 */
	public synchronized void stop() {
		this.senderOperation.queryBreak();
	}

	class SenderOperation extends AbstractLoopOperation {

		public SenderOperation() {
			super( PING_SLEEP_TIME );
			setDisplayName( "KAPE-Sender[" + KeepAlivePingSender.this.protocol.getDisplayName() + "]" );
		}

		/* (non-Javadoc)
		 * @see ss.common.operations.AbstractLoopOperation#performLoopAction()
		 */
		@Override
		protected void performLoopAction() throws OperationBreakException {
			KeepAlivePingEvent event = new KeepAlivePingEvent( "Created by " + KeepAlivePingSender.this.protocol.toString() + " at " + DateUtils.formatNowCanonicalDate() );
			if ( logger.isDebugEnabled() ) {
				logger.debug( "Firing " + event );
			}
			event.fireAndForget(KeepAlivePingSender.this.protocol);
		}

		/* (non-Javadoc)
		 * @see ss.common.operations.AbstractOperation#isDeamonDesired()
		 */
		@Override
		protected boolean isDeamonDesired() {
			return true;
		}

		
	}
}
