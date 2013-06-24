/**
 * 
 */
package ss.client.ui.preferences;

import org.eclipse.swt.widgets.Composite;

import ss.client.preferences.PreferencesController;
import ss.client.ui.SupraSphereFrame;

/**
 * @author zobo
 *
 */
public class SpheresPreferencesSimpleUserComposite extends
		SpheresPreferencesComposite {

	/**
	 * @param parent
	 * @param style
	 * @param controller
	 * @param sF
	 */
	public SpheresPreferencesSimpleUserComposite(Composite parent, int style,
			PreferencesController controller, SupraSphereFrame sF) {
		super(parent, style, controller, sF);
	}

	public PreferenceAbstractShellUnit getNewUnit(Composite parent, int style,
			String labelString, boolean selection, boolean allowModify) {
		return new PreferenceSimpleShellUnit(parent,  style,
				 labelString,  selection,  allowModify);
	}

}
