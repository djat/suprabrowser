/**
 * 
 */
package ss.client.ui.balloons;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import ss.client.ui.balloons.BalloonElement.BalloonTypes;

/**
 * @author zobo
 * 
 */
public class BalloonQueue {
	/**
	 * 
	 */
	private static final int BALLOONS_MAX_NUMBER = 9;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BalloonQueue.class);

	private final LinkedBlockingQueue<BalloonElement> promotedLine = new LinkedBlockingQueue<BalloonElement>(
			1);

	private final LinkedList<BalloonElement> waitingLine = new LinkedList<BalloonElement>();

	private BalloonElement urgentBalloon = null;

	private boolean active = false;

	void put(BalloonElement element) {
		synchronized (this.waitingLine) {
			if (element.getType() == BalloonTypes.DRAGANDDROP) {
				// Urgent open balloon only one at moment
				if (this.urgentBalloon == null){
					this.urgentBalloon = element;
					promote();
				}
			} else {
				logger.error("HERE: " + element.getMessageId());
				if (this.waitingLine.size() < BALLOONS_MAX_NUMBER) {
					boolean offer = true;
					for (BalloonElement existingElement : this.waitingLine) {
						if (existingElement.getMessageId().equals(
								element.getMessageId())) {
							offer = false;
							break;
						}
					}
					if (offer) {
						logger.error("offer: " + element.getMessageId());
						this.waitingLine.offer(element);
						if (!this.active) {
							promote();
						}
					}
				}
			}
		}
	}

	BalloonElement take() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to take next balloon element, waiting...");
			}
			BalloonElement element = this.promotedLine.take();
			this.active = true;
			return element;
		} catch (InterruptedException ex) {
			logger.error(ex);
			return null;
		}
	}

	void release() {
		synchronized (this.waitingLine) {
			if (logger.isDebugEnabled()) {
				logger.debug("Release and delete balloon element");
			}
			this.active = false;
			if (!this.waitingLine.isEmpty()) {
				this.waitingLine.removeFirst();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Elements left: " + this.waitingLine.size());
			}
			promote();
		}
	}

	void releaseUrgent() {
		synchronized (this.waitingLine) {
			if (logger.isDebugEnabled()) {
				logger.debug("Release and not delete balloon element");
			}
			this.active = false;
			this.urgentBalloon = null;
			if (logger.isDebugEnabled()) {
				logger.debug("Elements left: " + this.waitingLine.size());
			}
			promote();
		}
	}

	private void promote() {
		if (this.urgentBalloon != null){
			try {
				this.active = true;
				this.promotedLine.put(this.urgentBalloon);
			} catch (InterruptedException ex) {
				logger.error(ex);
				this.active = false;
			}
			return;
		}
		if (!this.waitingLine.isEmpty()) {
			this.active = true;
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Elements left: " + this.waitingLine.size());
				}
				this.promotedLine.put(this.waitingLine.peek());
			} catch (InterruptedException ex) {
				logger.error(ex);
				this.active = false;
			}
		}
	}
}
