/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public class CloseAllTabsSelectionListener implements SelectionListener {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CloseAllTabsSelectionListener.class);
	
	private SupraSphereFrame sF;
	
	public CloseAllTabsSelectionListener(SupraSphereFrame sF) {
		this.sF = sF;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {
	
	}

	public void widgetSelected(SelectionEvent arg0) {
		this.sF.closeAllTabs();		
	}

}
