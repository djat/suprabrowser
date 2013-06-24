package ss.client.ui.Listeners.browser;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import ss.client.ui.tempComponents.BrowserControlPanel;

public class BrowserControlPanelAddressFieldListener implements KeyListener {

	private BrowserControlPanel control = null;

	public BrowserControlPanelAddressFieldListener(BrowserControlPanel control) {
		this.control = control;
	}

	public void keyPressed(KeyEvent e) {

		if (e.keyCode == 13) {
			this.control.performLoad();
		}

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
