package ss.client.debug.deadlock;

import java.util.Date;

public class DeadlockMessagesListener implements IDeadlockMessagesListener{

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DeadlockMessagesListener.class);
	
	/* (non-Javadoc)
	 * @see ss.client.debug.deadlock.IDeadlockMessagesListener#dumpPossibleDeadlock(java.lang.String)
	 */
	public void dumpPossibleDeadlock(String dump) {
		logger.fatal( dump );			
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.deadlock.IDeadlockMessagesListener#ended()
	 */
	public void ended() {
		logger.info( "Deadlock guard ended " + new Date() );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.deadlock.IDeadlockMessagesListener#started()
	 */
	public void started() {
		logger.info( "Deadlock guard started " + new Date() );
	}

}
