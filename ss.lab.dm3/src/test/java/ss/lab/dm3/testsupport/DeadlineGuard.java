package ss.lab.dm3.testsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.builder.ToStringBuilder;
import junit.framework.Assert;

public class DeadlineGuard {
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	private final String message;

	private final AtomicInteger checkPointCount = new AtomicInteger(0);
	
	private final List<String> checkpoints = new ArrayList<String>();

	private long deadlineTime = 0;

	public DeadlineGuard(String message) {
		this.message = message;
	}

	/**
	 * @param timeout
	 */
	public synchronized void waitToAllCheckpoints(long timeout) {
		checkWaitIsNotStarted();
		this.deadlineTime = timeout + System.currentTimeMillis();
		for(;;) {
			if (this.isReleased()) {
				return;
			}
			long waitTime = this.deadlineTime - System.currentTimeMillis();
			if ( waitTime > 0 ) {
				try {
					this.wait(waitTime);
					if (this.isReleased()) {
						return;
					}
				} catch (InterruptedException ex) {
					if (this.log.isDebugEnabled()) {
						this.log.debug("wait to dispose interruped");
					}
				}
			}
			if (this.isReleased()) {
				return;
			}
			if ( waitTime <= 0 ) { 
				break;
			}
		}
		Assert.fail("Deadline guard failed " + this + ", used timeout is " + timeout );
	}

	public synchronized void addCheckpoint( String checkpoint ) {
		checkWaitIsNotStarted();
		this.checkpoints.add(checkpoint);
		this.checkPointCount.incrementAndGet();
	}
	
	public synchronized void passCheckpoint( String checkpoint ) {
		if ( !this.checkpoints.remove( checkpoint ) ) {
			throw new IllegalStateException( "Unexpected checkpoint " + checkpoint );
		}
		int count = this.checkPointCount.decrementAndGet();
		if ( count == 0 ) {
			this.notifyAll();
		}
	}

	/**
	 * 
	 */
	private synchronized void checkWaitIsNotStarted() {
		if (this.deadlineTime != 0) {
			throw new IllegalStateException(
					"Wait to all realease is started for " + this);
		}
	}


	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		return tsb.append("message", this.message).append( "realeaseCount", this.checkPointCount).toString();
	}

	/**
	 * @param deadlineListener
	 */
	public void addDeadlineListener(DeadlineListener deadlineListener) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param deadlineListener
	 */
	public void removeDeadlineListener(DeadlineListener deadlineListener) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * @return
	 */
	public boolean isReleased() {
		return this.checkPointCount.get() == 0;
	}

}

interface DeadlineListener {

	void success( DeadlineGuard deadline );
	
	void fail( DeadlineGuard deadline );
}

