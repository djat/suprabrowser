/**
 * 
 */
package ss.client.event;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.MessagesPane;
import ss.client.ui.docking.PreviewAreaDocking;
import ss.global.SSLogger;

/**
 * @author dankosedin
 * 
 */
public class HystorySelectionListener implements SelectionListener {

	private static Logger logger = SSLogger
			.getLogger(HystorySelectionListener.class);

	private boolean forward;

	private PreviewAreaDocking dock;

	public HystorySelectionListener(PreviewAreaDocking dock, boolean forward) {
		this.forward = forward;
		this.dock = dock;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	public void widgetSelected(SelectionEvent arg0) {
		logAll();
	}

	/**
	 * 
	 */
	private void logAll() {
		this.dock.getMessagesPane().getLocker().lock();	
		logger.info("                              ");
		logger.info("Hystory event recieved");
		logger.info("Traveling in future=" + this.forward);
		final MessagesPane messagesPane = this.dock.getMessagesPane();		
		final Document sphereDefinition = messagesPane.getSphereDefinition();
		logger.info("sphereDef=" + sphereDefinition.asXML());
		logger.info("uniqueId=" + messagesPane.getUniqueId());
		logger.info("message_pane=" + messagesPane.toString());
		Thread t = new Thread() {
			public void run() {
				if (processSphereDef(sphereDefinition)) {
					messagesPane.removeAll();
					messagesPane.client.searchSphere(messagesPane
							.getRawSession(), sphereDefinition, "true");
				}
			}

		};
		t.start();
	}

	private boolean processSphereDef(Document sphereDefinition) {
		Element page = sphereDefinition.getRootElement().element("paging")
				.element("page");
		if (page == null) {
			page = sphereDefinition.getRootElement().element("paging")
					.addElement("page").addAttribute("value", "" + 1);
		}
		int i = Integer.parseInt(page.attributeValue("value"));
		if (this.forward) {
			if (i > 1) {
				page.attribute("value").setValue("" + (i - 1));
			} else {
				return false;
			}
		} else {
			page.attribute("value").setValue("" + (i + 1));
		}
		Element mpId = sphereDefinition.getRootElement().element("search")
				.element("message_pane_id");
		String uniqueId = this.dock.getMessagesPane().getUniqueId();
		if (mpId == null) {
			sphereDefinition.getRootElement().element("search").addElement(
					"message_pane_id").addAttribute("value", uniqueId);
		} else {
			mpId.attribute("value").setValue(uniqueId);
		}
		return true;
	}

}
