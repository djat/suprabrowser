/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.common.XmlDocumentUtils;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class SaveOrderSelectionListener implements SelectionListener {

	private SupraSphereFrame sF;
	
	private static final Logger logger = SSLogger.getLogger(SaveOrderSelectionListener.class);
	
	public SaveOrderSelectionListener(SupraSphereFrame sF) {
		this.sF = sF;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	
	public void widgetSelected(SelectionEvent arg0) {

		logger.info("Start save");
		Document createDoc = DocumentHelper.createDocument();

		Element root = createDoc.addElement("build_order");
		int order = 0;
		for (final MessagesPane mp : this.sF.tabbedPane.getMessagesPanes() ) {
			try {
				String systemName = (String) (mp.getRawSession())
						.get("sphere_id");
				if (mp.getRawSession().get("query_id") == null) {
					final String displayName = this.sF.client
							.getVerifyAuth().getDisplayName(systemName);
					root.addElement("order").addAttribute("value",
							String.valueOf(order)).addAttribute(
							"display_name", displayName).addAttribute(
							"system_name", systemName);
					logger.info("HERES ROOT!: " + root.asXML());
				} else {
					final MessagesPane mpDefault = this.sF
							.getMessagesPaneWithoutQueryIdFromSphereId((String) mp
									.getRawSession().get("sphere_id"));
					if (mpDefault == null) {
						final String displayName = this.sF.client
								.getVerifyAuth().getDisplayName(systemName);
						root.addElement("order").addAttribute("value",
								String.valueOf(order)).addAttribute(
								"display_name", displayName).addAttribute(
								"system_name", systemName);
						logger.info("HERES ROOT!: " + root.asXML());
					}
				}

			} catch (Exception ex) {
				logger.error("Can't save message pane order", ex);
			}
			++ order;
		}
		logger.info("saved order : "+XmlDocumentUtils.toPrettyString(createDoc));
		this.sF.client.saveTabOrderToContact(this.sF.getMainRawSession(), createDoc);	
	}

}
