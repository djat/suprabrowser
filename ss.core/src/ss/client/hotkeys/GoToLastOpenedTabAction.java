package ss.client.hotkeys;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.tempComponents.SupraCTabItem;
import ss.client.ui.tempComponents.SupraCTabTable;
import ss.client.ui.tempComponents.interfaces.IShown;
import ss.common.UiUtils;
import ss.global.SSLogger;

public class GoToLastOpenedTabAction extends AbstractAction {

	public static final Logger logger = SSLogger
			.getLogger(GoToLastOpenedTabAction.class);

	public void performExecute() {
		UiUtils.swtBeginInvoke(new Runnable() {

			public void run() {
				SupraCTabTable tabPane = getSupraFrame().tabbedPane;
				Composite[] comps = tabPane.getComponents();

				for (Composite c : comps) {
					if (c instanceof IShown && !((IShown) c).isShown()) {
						tabPane.selectTabByPane(c);
						((IShown) c).setShown(true);
						getSupraFrame().getMenuBar().checkAddRemoveEnabled();

						SupraCTabItem item = tabPane.getSelectedSupraItem();
						if (item != null
								&& item.getBrowserPane().getBrowser() != null) {
							try {
								item.getBrowserPane().getBrowser().setFocus();
							} catch (NullPointerException npe) {

							}
						}
						return;
					}
				}
				tabPane.selectNextTab();
				getSupraFrame().getMenuBar().checkAddRemoveEnabled();
			}

		});
	}
}
