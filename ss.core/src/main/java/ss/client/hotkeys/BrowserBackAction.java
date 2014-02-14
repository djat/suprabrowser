package ss.client.hotkeys;

import ss.client.ui.tempComponents.SupraCTabItem;

public class BrowserBackAction extends AbstractAction {

	public void performExecute() {
		SupraCTabItem selectedItem = this.getSupraFrame().tabbedPane.getSelectedSupraItem();
		if (selectedItem != null) {
			if (selectedItem.getBrowserPane().getBrowser().isBackEnabled()) {
				
			selectedItem.getBrowserPane().getBrowser().back();
			}
		}
	}

}