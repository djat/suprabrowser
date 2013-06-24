/**
 * 
 */
package ss.framework.networking2;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

import ss.common.IdentityUtils;

/**
 * TODO:#implement ReplyDispatchers garbage collector
 */
final class ReplyHandlingManager {

	@SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReplyHandlingManager.class);
	
	private static final long SLEEP_TIME = 500;
	
	private final Hashtable<String,GuardedReplyDispatcher> intiatorSendIdToReplyDispatcher = new Hashtable<String,GuardedReplyDispatcher>(); 
	
	private final Thread checkerThread = new Thread( new Checker() );
	
	private final AtomicBoolean alive = new AtomicBoolean( true ); 

	
	/**
	 * @param protocolOwner
	 */
	public ReplyHandlingManager() {
		super();
	}

	/**
	 * @param replyDispatcher
	 */
	public final synchronized void registryReceiver(IReplyDispatcher replyDispatcher) {
		this.intiatorSendIdToReplyDispatcher.put(replyDispatcher.getInitiationSendId(), new GuardedReplyDispatcher( replyDispatcher ) );		
	}

	/**
	 * @param replyDispatcher
	 */
	public final synchronized void notifyCannotSendCommand(IReplyDispatcher replyDispatcher) {
		removeReceiver(replyDispatcher);
		replyDispatcher.cannotSendInitiationCommand();
	}

	/**
	 * @param replyDispatcher
	 * @param ex
	 */
	public final synchronized void notifyCannotSendCommand(IReplyDispatcher replyDispatcher, InterruptedException ex) {
		removeReceiver(replyDispatcher);
		replyDispatcher.cannotSendInitiationCommand( ex );
	}

	/**
	 * @param replyDispatcher
	 */
	private void removeReceiver(IReplyDispatcher replyDispatcher) {
		this.intiatorSendIdToReplyDispatcher.remove( replyDispatcher.getInitiationSendId() );		
	}

	/**
	 * @param protocol
	 * @param reply
	 */
	public final synchronized void dispatchReply(Reply reply) {
		String initiatorSendId = reply.getInitiatiorSendId();
		GuardedReplyDispatcher guardedReplyDispatcher = initiatorSendId != null ? this.intiatorSendIdToReplyDispatcher.get( initiatorSendId ) : null;
		if ( guardedReplyDispatcher != null ) {
			final IReplyDispatcher replyDispatcher = guardedReplyDispatcher.getReplyDispatcher(); 
			removeReceiver( replyDispatcher );
			replyDispatcher.dispachReply(reply);
		}
		else {
			logger.error( "Cannot find reply dispatcher by initiation command id. Reply is " + reply );
		}
	}

	public final synchronized void dispose() {
		this.shootdown();
		if ( this.intiatorSendIdToReplyDispatcher.size() > 0 ) {
			ArrayList<GuardedReplyDispatcher> replyDispatchersToDispose = new ArrayList<GuardedReplyDispatcher>( this.intiatorSendIdToReplyDispatcher.values() );
			for( GuardedReplyDispatcher replyDispatcher : replyDispatchersToDispose ) {
				notifyCannotSendCommand( replyDispatcher.getReplyDispatcher() );
			}
		}		
	}

	/**
	 * @param replyDispatcher
	 */
	synchronized void timeOut(IReplyDispatcher replyDispatcher) {
		if ( this.intiatorSendIdToReplyDispatcher.containsKey( replyDispatcher.getInitiationSendId() ) ) {
			if (logger.isDebugEnabled()) {
				logger.debug( "Command time out " + replyDispatcher );
			}
			removeReceiver( replyDispatcher );
			replyDispatcher.replyTimeOut();
		}	
		else {
			logger.warn( "Can't find replyDispatcher " + replyDispatcher.getInitiationSendId() + ", check size " + this.intiatorSendIdToReplyDispatcher.size() );
		}
	}
	
	private void checkTimeOuts() {
		ArrayList<GuardedReplyDispatcher> timeOutsGuards = null; 
		synchronized( this ) {
			for( GuardedReplyDispatcher guard : this.intiatorSendIdToReplyDispatcher.values() ) {
				if ( guard.isTimeOut() ) {
					if ( timeOutsGuards == null ) {
						timeOutsGuards = new ArrayList<GuardedReplyDispatcher>();
					}
					timeOutsGuards.add(guard);
					if (logger.isDebugEnabled()) {
						logger.debug( "Adding time outed guard " + guard  + ", check size" + this.intiatorSendIdToReplyDispatcher.size());
					}
				}
			}
		}
		if ( timeOutsGuards != null ) {
			for ( GuardedReplyDispatcher guard : timeOutsGuards ) {
				timeOut( guard.getReplyDispatcher() );
			}
		}
	}
	
	/**
	 * @return
	 */
	private boolean isAlive() {
		return this.alive.get();
	}
	
	
	
	
	/**
	 * 
	 */
	public void start(String baseName) {
		this.checkerThread.setDaemon( true );
		this.checkerThread.setName( IdentityUtils.getNextRuntimeId( baseName + "-command-reply-guard" ) );
		this.checkerThread.start();
	}	
	
	/**
	 * 
	 */
	private void shootdown() {
		this.alive.set( false );
		this.checkerThread.interrupt();
	}
	
	/**
	 * @param command
	 */
	public synchronized void afterCommandWasSent(Command command) {
		final String sendId = command.getSendId();
		final GuardedReplyDispatcher replyDispatcher = this.intiatorSendIdToReplyDispatcher.get( sendId );
		if ( replyDispatcher != null ) {
			replyDispatcher.initStatUpTime();
		}
		else {
			logger.error( "Can't find replyDispatcher by " + sendId );
		}
	}
	
	static class GuardedReplyDispatcher {

		final IReplyDispatcher replyDispatcher;
		
		long startUpTime = 0;
		
		/**
		 * @param replyDispatcher
		 */
		public GuardedReplyDispatcher(final IReplyDispatcher replyDispatcher) {
			super();
			this.replyDispatcher = replyDispatcher;			
		}

		/**
		 * @return
		 */
		public boolean isTimeOut() {
			if ( hasStartUpTime() ) {
				return System.currentTimeMillis() > this.replyDispatcher.getTimeout() + this.startUpTime;
			}
			else {
				return false;
			}
		}
		
		public void initStatUpTime() {
			if ( hasStartUpTime() ) {
				logger.error( "Start up time already defined");
			}
			else {
				this.startUpTime = System.currentTimeMillis();
			}
		}
		
		private boolean hasStartUpTime() {
			return this.startUpTime > 0;
		}

		/**
		 * @return the replyDispatcher
		 */
		public IReplyDispatcher getReplyDispatcher() {
			return this.replyDispatcher;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Guard for " + this.replyDispatcher.getInitiationSendId();
		}

		
	}

	/**
	 *
	 */
	class Checker implements Runnable {


		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			while( isAlive() ) {
				checkTimeOuts();
				try {
					Thread.sleep( SLEEP_TIME );
				} catch (InterruptedException ex) {
					//Do nothing 
				}
			}
		}
	}
	
}
