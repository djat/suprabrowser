/**
 * 
 */
package ss.client.ui.Listeners.browser;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.tempComponents.BrowserPane;

/**
 * @author roman
 *
 */
public class BrowserControlPanelStartListener implements SelectionListener {

	BrowserPane bp;
	
	public BrowserControlPanelStartListener(BrowserPane bp) {
		this.bp = bp;
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	
	public void widgetSelected(SelectionEvent e) {
		this.bp.getBrowser().setUrl(this.bp.getStartURL());
	}

}
