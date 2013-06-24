/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import ss.client.networking.DialogsMainCli;
import ss.util.AdministrationUtils;

/**
 * @author roman
 *
 */
public class ModerationUtils {

	public static final ModerationUtils INSTANCE = new ModerationUtils();
	
	private ModerationUtils() {
		
	}
	
	public boolean canModerate(final DialogsMainCli client, final String systemName, final String name) {
		return AdministrationUtils.canModerate(client, systemName, name);
	}
}
