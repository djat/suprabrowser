/**
 * 
 */
package ss.client.ui.tempComponents;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.BrowserDataSource;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.docking.SBrowserDocking;
import ss.client.ui.docking.SupraDockingManager;
import ss.client.ui.docking.SupraSearchControlPanelDocking;
import swtdock.PartDragDrop;

/**
 * @author roman
 *
 */
public class SupraSearchPane extends AbstractShowablePane {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraSearchPane.class);
	
	private SupraDockingManager dockingManager;

	private SupraSphereFrame sF;

	private SupraSearchControlPanelDocking controlPanel;

	private SBrowserDocking browserDocking;

	private MessagesPane mP;

	private String startURL;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SupraSearchPane(Composite arg0, SupraSphereFrame sF, MessagesPane mP, BrowserDataSource source) {
		super(arg0, SWT.NONE);
		this.sF = sF;
		this.mP = mP;
		initGUI(source);
	}

	private void initGUI(BrowserDataSource source) {
		this.dockingManager = new SupraDockingManager(this, SWT.CLOSE);

		this.controlPanel = new SupraSearchControlPanelDocking(this);
		this.dockingManager.addPart(this.controlPanel);

		this.browserDocking = new SBrowserDocking(this.dockingManager, true);
		this.browserDocking.setMP(this.mP);
		this.dockingManager.addPart(this.browserDocking);

		this.dockingManager.movePart(this.controlPanel, PartDragDrop.TOP,
				this.browserDocking, (float) 0.01);

		this.controlPanel.getContent().activate(
				this.browserDocking.getContent(), source);
	}

	public SupraBrowser getBrowser() {
		logger.debug("call browser");
		return this.browserDocking.getContent();
	}

	public SupraSearchControlPanel getControlPanel() {
		return this.controlPanel.getContent();
	}

	public SBrowserDocking getBrowserDocking() {
		return this.browserDocking;
	}

	public MessagesPane getMessagesPane() {
		return this.mP;
	}

	public void setStartURL(String url) {
		this.startURL = url;
	}

	public String getStartURL() {
		return this.startURL;
	}

	public SupraDockingManager getDockingManager() {
		return this.dockingManager;
	}

	public SupraSphereFrame getSF() {
		return this.sF;
	}
}
