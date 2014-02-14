/**
 * 
 */
package ss.client.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.AbstractControlPanel;

/**
 * @author zobo
 *
 */
public class PreferencesSupporter {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreferencesSupporter.class);
	
	private static final ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_PREFERENCESSUPPORTER);
	
	private static final String POPUP = "PREFERENCESSUPPORTER.POPUP";
	
	private static final String NOT_POPUP = "PREFERENCESSUPPORTER.NOT_POPUP";
	
	private static class PreferencesOnTopBeahaviorSupporter{
		
		private static String[] strings;
		
		static {
			strings = new String[2];
			strings[0] = bundle.getString(POPUP);
			strings[1] = bundle.getString(NOT_POPUP);
		}
		
		static List<String> get(){
			ArrayList<String> types = new ArrayList<String>(2);
			types.add(strings[0]);
			types.add(strings[1]);
			return types;
		}
		
		static String get(boolean value){
			String str;
			if (value){
				str = strings[0];
			} else {
				str = strings[1];
			}
			return str;
		}
		
		static boolean get(String value){
			boolean bool;
			if (value == null){
				bool = false;
			}
			if (strings[0].equals(value)){
				bool = true;
			} else {
				bool = false;
			}
			return bool;
		}
	}
	/**
	 * @return
	 */
	public static List<String> getDeliveryTypes() {
		ArrayList<String> types = new ArrayList<String>(2);
		types.add(AbstractControlPanel.bundle.getString(AbstractControlPanel.NORMAL));
		types.add(AbstractControlPanel.bundle.getString(AbstractControlPanel.CONFIRM_RECEIPT));
		return types;
	}
	
	/**
	 * @return
	 */
	public static List<String> getPopUpBehaviorStrings() {
		return PreferencesOnTopBeahaviorSupporter.get();
	}
	
	public static String getPopUpBehaviourName(boolean value){
		return PreferencesOnTopBeahaviorSupporter.get(value);
	}
	
	public static boolean getPopUpBehaviourValue(String name){
		return PreferencesOnTopBeahaviorSupporter.get(name);
	}
}
