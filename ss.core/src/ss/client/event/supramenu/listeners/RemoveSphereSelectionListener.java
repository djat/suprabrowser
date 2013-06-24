/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.apache.log4j.Logger;
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
public class RemoveSphereSelectionListener implements SelectionListener {

	private SupraSphereFrame sF;
	private static final Logger logger = SSLogger.getLogger(RemoveSphereSelectionListener.class);
	
	public RemoveSphereSelectionListener(SupraSphereFrame sF) {
		this.sF =sF;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	
	public void widgetSelected(SelectionEvent arg0) {
		logger.info("attempt removing current sphere from favourites");
		
		MessagesPane toRemove = this.sF.tabbedPane.getSelectedMessagesPane();
		if (toRemove == null) {
			logger.warn("Selected message pane is null");
			return;
		}
		this.sF.getMenuBar().removeFromFavourites(toRemove);
		this.sF.getMenuBar().getAddSphereItem().setEnabled(true);
		this.sF.getMenuBar().getRemoveSphereItem().setEnabled(false);
		
		String id = SphereStatement.wrap(toRemove.getSphereDefinition()).getSystemName();
		this.sF.client.removeSphereFromFavourites(id);
	}

}
