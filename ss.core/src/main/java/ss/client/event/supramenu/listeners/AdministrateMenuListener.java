/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.preferences.PreferencesUILoader;
import ss.client.preferences.PreferencesUILoader.OptionsTypes;

/**
 * @author zobo
 *
 */
public class AdministrateMenuListener implements SelectionListener {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreferencesMenuListener.class);

	public AdministrateMenuListener() {
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	public void widgetSelected(SelectionEvent se) {
		logger.info("Preferences menu selected");
		PreferencesUILoader.INSTANCE.load(OptionsTypes.ADMINISTRATE);
	}
}
