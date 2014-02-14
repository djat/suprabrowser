package ss.client.hotkeys;

import ss.client.ui.tempComponents.SupraCTabItem;

public class MoveCursorToInputBarAction extends AbstractAction {

	public void performExecute() {
		if (!getSupraFrame().tabbedPane.setFocusToCurrentSendField()) {
			SupraCTabItem item = getSupraFrame().tabbedPane
					.getSelectedSupraItem();
			if (item != null && item.getBrowserPane() != null) {
				item.getBrowserPane().getControlPanel().getAddressField()
						.setFocus();
				item.getBrowserPane().getControlPanel().getAddressField()
						.selectAll();
			}
		}
	}

}
