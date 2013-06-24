/**
 * 
 */
package ss.client.ui.Listeners.browser;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.BrowserPane;
import ss.client.ui.tempComponents.SavePageWindow;
import ss.client.ui.tempComponents.SpheresCollectionByTypeObject;

/**
 * @author roman
 *
 */
public class BrowserControlPanelSaveListener implements SelectionListener {

	private BrowserPane bp;
	
	public BrowserControlPanelSaveListener(BrowserPane bp) {
		this.bp = bp;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	
	public void widgetSelected(SelectionEvent se) {
		new Thread() {
			@Override
			public void run() {
				SpheresCollectionByTypeObject sphereOwner = new SpheresCollectionByTypeObject(SupraSphereFrame.INSTANCE.client);
				SavePageWindow.showDialog(BrowserControlPanelSaveListener.this.bp.getBrowser(), sphereOwner);
			}
		}.start();
		
	}

}
