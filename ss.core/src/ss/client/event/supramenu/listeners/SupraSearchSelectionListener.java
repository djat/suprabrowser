/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.LuceneSearchDialog;
import ss.client.ui.SupraSphereFrame;
import ss.global.SSLogger;

/**
 * @author roman
 * 
 */
public class SupraSearchSelectionListener implements SelectionListener {

	private SupraSphereFrame sF;

	private String sphereId = null;

	private static final Logger logger = SSLogger
			.getLogger(SupraSearchSelectionListener.class);

	public SupraSearchSelectionListener(SupraSphereFrame sF, String sphereId) {
		this.sF = sF;
		this.sphereId = sphereId;
	}

	public SupraSearchSelectionListener(SupraSphereFrame sF) {
		this(sF, null);
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	public void widgetSelected(SelectionEvent arg0) {
		logger.info("Mouse pressed...");
		LuceneSearchDialog lS = new LuceneSearchDialog(this.sF.client);
		if (this.sphereId != null) {
			lS.setSearchInSphere(this.sphereId);
		}
		lS.show(this.sF.getShell());

	}

}
