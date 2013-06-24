package ss.client.ui.relation.sphere.manage;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ss.client.localization.LocalizationLinks;

class Messages {
	
	private static final String BUNDLE_NAME = LocalizationLinks.SS_CLIENT_UI_RELATION_SPHERE_MANAGE; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
