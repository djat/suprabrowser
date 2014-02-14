package ss.client.hotkeys;

import java.util.ResourceBundle;

import ss.client.localization.LocalizationLinks;
import ss.common.UiUtils;

public class NewBrowserTabAction extends AbstractAction {

	private static final String SUPRASPHERE = "OPENBLANKSELECTIONLISTENER.SUPRASPHERE";

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_EVENT_SUPRAMENU_LISTENERS_OPENBLANKSELECTIONLISTENER);

	public void performExecute() {

		UiUtils.swtBeginInvoke(new Runnable() {

			@SuppressWarnings("unchecked")
			public void run() {

				getSupraFrame().addBlankMozillaTab(
						getSupraFrame().client.session,
						NewBrowserTabAction.this.bundle.getString(SUPRASPHERE));
			}

		});

	}

}
