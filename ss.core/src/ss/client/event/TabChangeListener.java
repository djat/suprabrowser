/*
 * Created on Apr 28, 2004
 */
package ss.client.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.BrowserPane;
import ss.client.ui.tempComponents.SupraCTabItem;
import ss.client.ui.tempComponents.SupraCTabTable;
import ss.client.ui.tempComponents.interfaces.IShown;

/**
 * @author david
 * 
 */
public class TabChangeListener implements SelectionListener {

	private SupraCTabTable tabbedPane = null;

	private SupraSphereFrame sF = null;

	//private int previousIndex = 0;

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TabChangeListener.class);

	public TabChangeListener(SupraCTabTable tabbedPane, SupraSphereFrame sF) {
		this.tabbedPane = tabbedPane;
		this.sF = sF;
	}

	private void stateChanged() {
		logger.info("Tab is changed");
		try {
			this.sF.getCommentWindowController().disposeCommentWindow();
			final SupraCTabItem selectedItem = this.tabbedPane.getSelectedSupraItem();
			if ( selectedItem == null ) {
				return;
			}
			this.sF.getMenuBar().checkAddRemoveEnabled();
			Composite comp = selectedItem.getPane();
			if (comp instanceof IShown && !((IShown) comp).isShown()) {
				((IShown) comp).setShown(true);
			}
			logger.info("Selected " + selectedItem );
			final int type = selectedItem.getContent();
			if (type == SupraCTabItem.CONTENT_MESSAGES_PANE) {
				messagesPaneTabChousen(selectedItem);
			} else if (type == SupraCTabItem.CONTENT_BROWSER) {
				BrowserPane browserPane = selectedItem.getBrowserPane();
				if ( browserPane != null ) {
					browserPane.setFocus();
				}
			}
		} catch (Exception ex) {
			logger.error("Can't process state changes", ex);
		}
	}

	/**
	 * @param index
	 */
	private void messagesPaneTabChousen(SupraCTabItem item) {
		String title = item.getText();
		MessagesPane mp = item.getMessagesPane();
		
		mp.getPreviewAreaDocking().shakeBrowser();
		mp.getMessagesTree().update();
		mp.getMessagesTree().scrollToTop();
		
		if (mp.getControlPanel() instanceof ControlPanel) {
			((ControlPanel) mp.getControlPanel()).setFocusToSendField();
		}
		String sphere_id = mp.getSphereId();
		mp.recheckPeopleListColors();
		logger.info("Calling show for: " + title + "." + sphere_id);
	}

	public void widgetSelected(SelectionEvent arg0) {
		stateChanged();
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
	}
}
