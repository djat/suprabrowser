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
import ss.client.ui.tempComponents.MessagesPanePositionsInformation;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class SavePositionSelectionListener implements SelectionListener {

	private SupraSphereFrame sF;
	
	private static final Logger logger = SSLogger.getLogger(SavePositionSelectionListener.class);
	
	public SavePositionSelectionListener(SupraSphereFrame sF) {
		this.sF = sF;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	
	public void widgetSelected(SelectionEvent arg0) {

		logger.info("Start save");
		Document createDoc = DocumentHelper.createDocument();
		Element root = createDoc.addElement("window_position");
		
		int order = 0;
		for (final MessagesPane mp : this.sF.tabbedPane.getMessagesPanes() ) {
			double div0 = .45, div1 = 0.20, div2 = .45, div3 = 0.80;
			MessagesPanePositionsInformation pos = mp.calculateDivs();
			div0 = pos.getDiv0();
			div1 = pos.getDiv1();
			div2 = pos.getDiv2();
			div3 = pos.getDiv3();

			try {
				String systemName = (String) (mp.getRawSession())
						.get("sphere_id");

				if (mp.getRawSession().get("query_id") == null) {

					logger.warn("its query id was null");

					String displayName = this.sF.client
							.getVerifyAuth().getDisplayName(systemName);
					logger.info("HERES INFO: " + systemName + " : "
							+ displayName + " : " + div0 + " : " + div1
							+ " : " + div2 + " : " + div3);
					root.addElement("order").addAttribute("value",
							(new Integer(order)).toString()).addAttribute(
							"display_name", displayName).addAttribute(
							"system_name", systemName).addAttribute("div0",
							(new Double(div0)).toString()).addAttribute(
							"div1", (new Double(div1)).toString())
							.addAttribute("div2",
									(new Double(div2)).toString())
							.addAttribute("div3",
									(new Double(div3)).toString());
					logger.info("HERES ROOT!: " + root.asXML());
				} else {

					logger.warn("its query id was not null");

					MessagesPane mpDefault = this.sF
							.getMessagesPaneWithoutQueryIdFromSphereId((String) mp
									.getRawSession().get("sphere_id"));
					if (mpDefault == null) {

						String displayName = this.sF.client
								.getVerifyAuth().getDisplayName(systemName);
						logger.info("HERES INFO: " + systemName + " : "
								+ displayName + " : " + div0 + " : " + div1
								+ " : " + div2 + " : " + div3);
						root.addElement("order").addAttribute("value",
								(new Integer(order)).toString()).addAttribute(
								"display_name", displayName).addAttribute(
								"system_name", systemName).addAttribute(
								"div0", (new Double(div0)).toString())
								.addAttribute("div1",
										(new Double(div1)).toString())
								.addAttribute("div2",
										(new Double(div2)).toString())
								.addAttribute("div3",
										(new Double(div3)).toString());

					}

				}
			} catch (Exception exep) {
				logger.error( "Can't save position for messages pane", exep);
			}
			++ order;
		}
		this.sF.client.saveWindowPositionToContact(this.sF.getMainRawSession(), createDoc);
	}

}
