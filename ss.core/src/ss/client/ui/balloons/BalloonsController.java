/**
 * 
 */
package ss.client.ui.balloons;

import org.dom4j.Document;

import ss.client.ui.MessagesPane;
import ss.client.ui.balloons.BalloonElement.BalloonTypes;
import ss.common.ThreadUtils;
import ss.common.UiUtils;

/**
 * @author zobo
 * 
 */
public class BalloonsController {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BalloonsController.class);

	public static final BalloonsController INSTANCE = new BalloonsController();

	private BalloonsController() {
		this.queue = new BalloonQueue();
		ThreadUtils.startDemon(new Thread() {

			@Override
			public void run() {
				logger.info("Balloons Controller Demon Tread started");
				while (true) {
					showNextBalloon();
				}
			}

		}, BalloonsController.class);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private final BalloonQueue queue;

	private BalloonWindow activeBalloonWindow = null;

	private Object sync = new Object();

	/**
	 * @param pane
	 * @param emailDocument
	 * @param tempDoc
	 */
	public void addBalloon(Document doc, Document replyDoc, MessagesPane pane) {
		this.queue
				.put(BalloonFactory.createBalloonElement(doc, replyDoc, pane));
	}

	/**
	 * @param mp_new
	 * @param firstDoc
	 * @param b
	 */
	public void addBalloon(Document doc, boolean author, MessagesPane pane) {
		this.queue.put(BalloonFactory.createBalloonElement(doc, author, pane));
	}

	public void addDragAndDropBalloon(MessagesPane pane) {
		this.queue.put(BalloonFactory.createDragAndDropBalloonElement(pane));
	}

	private void closeBalloonWindow(boolean urgent) {
		synchronized (this.sync) {
			if (logger.isDebugEnabled()) {
				logger.debug("Closed Active Balloon");
			}
			this.activeBalloonWindow = null;
			if (urgent) {
				this.queue.releaseUrgent();
			} else {
				this.queue.release();
			}
		}
	}

	private void setBalloonWindow(BalloonWindow balloonWindow) {
		synchronized (this.sync) {
			if (logger.isDebugEnabled()) {
				logger.debug("Setting next active Balloon");
			}
			this.activeBalloonWindow = balloonWindow;
		}
	}

	private void showNextBalloon() {
		BalloonElement element = this.queue.take();
		final boolean urgent;
		if (element.getType() == BalloonTypes.DRAGANDDROP) {
			if (this.activeBalloonWindow != null){
				UiUtils.swtInvoke(new Runnable(){

					public void run() {
						BalloonsController.this.activeBalloonWindow.close();
					}
					
				});
				this.activeBalloonWindow = null;
			}
			urgent = true;
		} else {
			urgent = false;
		}
		BalloonFactory.createBalloon(element, new IBalloonListener() {

			public void closed() {
				closeBalloonWindow(urgent);
			}

			public void created(BalloonWindow setBalloon) {
				setBalloonWindow(setBalloon);
			}

		});
	}

	/**
	 * @param b
	 *            shows or hide balloon
	 */
	public void setShown(boolean b) {
		synchronized (this.sync) {
			if (this.activeBalloonWindow != null) {
				this.activeBalloonWindow.showOrHide(true);
			}
		}
	}

}
