/**
 * 
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.UiUtils;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class SphereAccessDeniedMessageHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereAccessDeniedMessageHandler.class);
	
	public SphereAccessDeniedMessageHandler(DialogsMainCli client) {
	}
	
	/* (non-Javadoc)
	 * @see ss.common.ProtocolHandler#getProtocol()
	 */
	public String getProtocol() {
		return SSProtocolConstants.ACCESS_DENIED;
	}

	/* (non-Javadoc)
	 * @see ss.common.ProtocolHandler#handle(java.util.Hashtable)
	 */
	public void handle(Hashtable update) {
		final String displayName = (String)update.get(SessionConstants.DISPLAY_NAME);
		final String sphereId = (String)update.get(SessionConstants.SPHERE_ID);
		
		SphereOpenManager.INSTANCE.responceSphereDenied(sphereId);
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				UserMessageDialogCreator.errorSphereAccessDenied(displayName);
			}
		});	
	}
}
