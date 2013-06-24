package ss.client.hotkeys;

import ss.client.ui.tempComponents.SupraCTabItem;

public class CloseBrowserTabAction extends AbstractAction {
	
	public void performExecute() {
		SupraCTabItem selectedItem = this.getSupraFrame().tabbedPane.getSelectedSupraItem();
		
		if (selectedItem != null && !selectedItem.isRootPane()) {
			selectedItem.safeClose(); 
		}
	}

}
