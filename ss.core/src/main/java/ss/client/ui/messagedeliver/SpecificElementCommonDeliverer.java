/**
 * 
 */
package ss.client.ui.messagedeliver;

import java.util.ArrayList;
import java.util.List;

import ss.client.ui.MessagesPane;
import ss.common.ThreadUtils;

/**
 * @author zobo
 * 
 */
class SpecificElementCommonDeliverer {

	private class WaitingSpecificDeliveringElement {
		AbstractDeliveringElement element;

		MessagesPane pane;

		WaitingSpecificDeliveringElement(AbstractDeliveringElement element,
				MessagesPane pane) {
			super();
			this.element = element;
			this.pane = pane;
		}
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SpecificElementCommonDeliverer.class);

	private List<WaitingSpecificDeliveringElement> waitingList;

	private Thread input;

	static SpecificElementCommonDeliverer INSTANCE = new SpecificElementCommonDeliverer();

	private SpecificElementCommonDeliverer() {
		this.waitingList = new ArrayList<WaitingSpecificDeliveringElement>();
		this.input = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						sleep(100);
						check();
					} catch (InterruptedException ex) {
						logger.error(ex);
					}
				}
			}
		};
		ThreadUtils
				.startDemon(this.input, "Common Deliverer for not yet loaded spheres");
	}

	void put(AbstractDeliveringElement element, MessagesPane pane) {
		synchronized (this.waitingList) {
			if (logger.isDebugEnabled()){
				logger.debug("recieved in Common pre delivering center " + DeliverersManager.FACTORY.getLogInfo(element));
			}
			this.waitingList.add(new WaitingSpecificDeliveringElement(element,
					pane));
		}
	}

	private void check() {
		synchronized (this.waitingList) {
			if (this.waitingList.isEmpty()) {
				return;
			}

			List<WaitingSpecificDeliveringElement> toRemove = new ArrayList<WaitingSpecificDeliveringElement>();
			for (WaitingSpecificDeliveringElement wElement : this.waitingList) {
				if (DeliverersManager.INSTANCE.check(wElement.element,
						wElement.pane)) {
					toRemove.add(wElement);
				}
			}
			this.waitingList.removeAll(toRemove);
		}
	}
}
