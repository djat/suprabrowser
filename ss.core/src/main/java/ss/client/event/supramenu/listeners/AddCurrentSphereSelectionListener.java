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
import ss.domainmodel.SphereStatement;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class AddCurrentSphereSelectionListener implements SelectionListener {

	private static final Logger logger = SSLogger.getLogger(AddCurrentSphereSelectionListener.class);
	private SupraSphereFrame sF;
	
	private static final String FAVOURITES = "favourites";
	private static final String SPHERE = "sphere";
	private static final String DISPLAY_NAME = "display_name";
	private static final String SYSTEM_NAME = "system_name";
	
	public AddCurrentSphereSelectionListener(SupraSphereFrame sF) {
		this.sF = sF;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	
	public void widgetSelected(SelectionEvent arg0) {
		logger.info("start adding current sphere to favourites");
		final MessagesPane toAdd = this.sF.tabbedPane.getSelectedMessagesPane();
		if (toAdd == null) {
			logger.warn("Selected message pane is null");
			return;
		}
		SphereStatement sphereSt = SphereStatement.wrap(toAdd.getSphereDefinition());
		
		Document createDoc = DocumentHelper.createDocument();
		Element root = createDoc.addElement(FAVOURITES);
		try {
			String systemName = sphereSt.getSystemName();
			String displayName = sphereSt.getDisplayName();

			root.addElement(SPHERE).addAttribute(DISPLAY_NAME, displayName).addAttribute(
					SYSTEM_NAME, systemName);
			logger.info("HERES ROOT!: " + root.asXML());
		} catch (Exception exep) {
			logger.error(exep.getMessage(), exep);
		}

		this.sF.client.addSphereToFavourites(this.sF.getMainRawSession(), createDoc);
		
		this.sF.getMenuBar().addToFavourites(toAdd);
		this.sF.getMenuBar().getAddSphereItem().setEnabled(false);
		this.sF.getMenuBar().getRemoveSphereItem().setEnabled(true);
	}
	

}
