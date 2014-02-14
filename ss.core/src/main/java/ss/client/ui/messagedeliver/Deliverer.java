/**
 * 
 */
package ss.client.ui.messagedeliver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ss.client.ui.MessagesPane;
import ss.common.ThreadUtils;

/**
 * @author zobo
 * 
 */
public class Deliverer {
	
	private class BlockedPane {
		MessagesPane pane = null;
		
		private LinkedList<AbstractDeliveringElement> blockedList = new LinkedList<AbstractDeliveringElement>();

		public BlockedPane(MessagesPane specificPane) {
			this.pane = specificPane;
		}

		void put(AbstractDeliveringElement element){
			this.blockedList.offer(element);
		}
		
		LinkedList<AbstractDeliveringElement> get(){
			return this.blockedList;
		}
	}
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Deliverer.class);

	private final List<MessagesPane> panes;
	
	private final List<BlockedPane> blockedPanes;
	
	private final String sphereId;

	private final Thread deliver;

	private final MessagesLine messagesQueue;

	private boolean blocked = true;
	
	private boolean alive = true;

	Deliverer(String sphereId) {
		if (logger.isDebugEnabled()) {
			logger.debug("Deliverer started for: " + sphereId);
		}
		this.sphereId = sphereId;
		this.panes = new ArrayList<MessagesPane>();
		this.messagesQueue = new MessagesLine();
		this.blockedPanes = new ArrayList<BlockedPane>();
		this.deliver = new Thread() {
			@Override
			public void run() {
				while (Deliverer.this.alive) {
					if (Deliverer.this.blocked) {
						try {
							sleep(100);
						} catch (InterruptedException ex) {
							logger.error("Interrupted");
						}
					} else {
						processNext();
					}
				}
			}
		};
		ThreadUtils.startDemon(this.deliver, "Messages deliverer for sphereId: " + sphereId);
	}

	private void processNext() {

		AbstractDeliveringElement element = this.messagesQueue.take();
		List<MessagesPane> localPanes = null;
		AtomicReference<LinkedList<AbstractDeliveringElement>> releasedList = new AtomicReference<LinkedList<AbstractDeliveringElement>>();
		synchronized (this.panes) {
			
			localPanes = getPanes(element, releasedList);
		}
		if (releasedList.get() == null){
			if (logger.isDebugEnabled()) {
				logger.debug("Suggesting to pocessing element " + DeliverersManager.FACTORY.getLogInfo(element));
			}
			DeliverProcessor.process(element, localPanes);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Processing with released list element " + DeliverersManager.FACTORY.getLogInfo(element));
			}
			DeliverProcessor.process(element, localPanes);
			for (AbstractDeliveringElement el : releasedList.get()){
				if (logger.isDebugEnabled()) {
					logger.debug("Processing as element from release list element " + DeliverersManager.FACTORY.getLogInfo(el));
				}
				DeliverProcessor.process(el, localPanes);
			}
		}
	}

	private ArrayList<MessagesPane> getPanes(final AbstractDeliveringElement element, final AtomicReference<LinkedList<AbstractDeliveringElement>> releasedList) {
		if (element.getSpecific() != null) {
				ArrayList<MessagesPane> toRet = new ArrayList<MessagesPane>();
				if (isMessagesPane(element.getSpecific())) {
					synchronized (this.blockedPanes) {
						BlockedPane blockedPane = releaseBlocked(element.getSpecific());
						toRet.add(element.getSpecific());
						if (blockedPane != null){
							if (logger.isDebugEnabled()) {
								logger.debug("Setting release list size: " + blockedPane.get().size());
							}
							releasedList.set(blockedPane.get());
						}
					}
				}
				return toRet;
		} else {
			synchronized (this.blockedPanes) {
				if (this.blockedPanes.isEmpty()){
					return new ArrayList<MessagesPane>(this.panes);
				} else {
					ArrayList<MessagesPane> toRet = new ArrayList<MessagesPane>();
					for (MessagesPane pane : this.panes){
						if (!processIsBlocked(element, pane)){
							toRet.add(pane);
						}
					}
					return toRet;
				}
			}
		}
	}
	
	/**
	 * @param specific
	 */
	private BlockedPane releaseBlocked(MessagesPane specific) {
		BlockedPane toDel = null;
		for (BlockedPane bpane : this.blockedPanes){
			if (bpane.pane == specific){
				toDel = bpane;
				break;
			}
		}
		if (toDel != null){
			this.blockedPanes.remove(toDel);
		}
		return toDel;
	}

	private boolean processIsBlocked(AbstractDeliveringElement element, MessagesPane pane){
		for (BlockedPane blockedPane : this.blockedPanes){
			if (blockedPane.pane == pane){
				if (logger.isDebugEnabled()) {
					logger.debug("Adding to block list "  + DeliverersManager.FACTORY.getLogInfo(element));
				}
				blockedPane.put(element);
				return true;
			}
		}
		return false;
	}

	void deliver(AbstractDeliveringElement element) {
		this.messagesQueue.put(element);
	}

	void stop() {
		this.deliver.interrupt();
	}

	/**
	 * @param pane
	 */
	void addMessagesPane(MessagesPane pane) {
		synchronized (this.panes) {
			if (pane != null) {
				this.panes.add(pane);
				if (this.blocked) {
					this.blocked = false;
				}
			}
		}
	}

	void removeMessagesPane(MessagesPane pane) {
		synchronized (this.panes) {
			if (pane != null) {
				this.panes.remove(pane);
			}
		}
	}

	String getSphereId() {
		return this.sphereId;
	}

	boolean isEmptyMessagesPanes() {
		return this.panes.isEmpty();
	}

	/**
	 * @param pane
	 */
	boolean isMessagesPane(MessagesPane pane1) {
		synchronized (this.panes) {
			for (MessagesPane pane : this.panes) {
				if (pane == pane1) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * @param element
	 * @param pane
	 */
	void deliver(AbstractDeliveringElement element, MessagesPane pane) {
		synchronized (this.blockedPanes) {
			element.setSpecific(pane);
			this.messagesQueue.put(element);
		}
	}

	/**
	 * @param specificPane
	 */
	void block(MessagesPane specificPane) {
		synchronized (this.blockedPanes) {
			BlockedPane p = new BlockedPane(specificPane);
			this.blockedPanes.add(p);
		}
	}

	/**
	 * 
	 */
	void kill() {
		this.alive = false;
		this.deliver.interrupt();
	}
}
