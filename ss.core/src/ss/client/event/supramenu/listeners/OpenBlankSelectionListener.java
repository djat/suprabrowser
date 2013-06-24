/**
 * 
 */
package ss.client.event.supramenu.listeners;

import java.util.ResourceBundle;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public class OpenBlankSelectionListener implements SelectionListener {

	private SupraSphereFrame sF;

	private static final String SUPRASPHERE = "OPENBLANKSELECTIONLISTENER.SUPRASPHERE";

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_EVENT_SUPRAMENU_LISTENERS_OPENBLANKSELECTIONLISTENER);

	public OpenBlankSelectionListener(SupraSphereFrame sF) {
		this.sF = sF;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	public void widgetSelected(SelectionEvent se) {
		this.sF.addBlankMozillaTab(this.sF.client.session, this.bundle
				.getString(SUPRASPHERE));
	}

}
