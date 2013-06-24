/**
 * 
 */
package ss.client.event.supramenu.listeners;

import java.util.Hashtable;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public class SaveGlobalMarkSelectionListener implements SelectionListener {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveGlobalMarkSelectionListener.class);
	
	private SupraSphereFrame sF;
	
	public SaveGlobalMarkSelectionListener(SupraSphereFrame sF) {
		this.sF = sF;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent arg0) {

		MessagesPane mp = (MessagesPane) this.sF.tabbedPane
				.getSelectedMessagesPane();
		if (mp == null) {
			logger.warn("Selected message pane is null");
			return;
		}
		Hashtable session = mp.getRawSession();

		this.sF.client.saveMarkForSphere(session,
				"global");

	}

}
