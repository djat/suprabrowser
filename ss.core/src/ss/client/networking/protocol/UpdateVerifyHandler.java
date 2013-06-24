/**
 * Jul 5, 2006 : 1:18:09 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import ss.client.event.SphereHierarchyUpdater;
import ss.client.networking.DialogsMainCli;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.ThreadUtils;
import ss.common.VerifyAuth;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class UpdateVerifyHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(UpdateVerifyHandler.class);
	
	private final DialogsMainCli cli;

	public UpdateVerifyHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleUpdateVerify(final Hashtable update) {
//		int groupSpheresCount = 0;
//		if(this.cli.getVerifyAuth()!=null) {
//			groupSpheresCount = this.cli.getVerifyAuth().getAllGroupSpheres().size();
//		}
//		
		
		if (logger.isDebugEnabled()) {
			logger.debug("handle verify updating");
		}
		
		VerifyAuth oldVerifyAuth = this.cli.getVerifyAuth();
		
		VerifyAuth newVerifyAuth = (((VerifyAuth) update
				.get(SessionConstants.VERIFY_AUTH)));
		
		this.cli.setVerifyAuth(newVerifyAuth);
		if (logger.isDebugEnabled()) {
			logger.debug("Update verify auth " + newVerifyAuth.getSupraSphere() );
		}		
		
		if ( oldVerifyAuth != null ) {
			SphereHierarchyUpdater updater = new SphereHierarchyUpdater(this.cli, oldVerifyAuth, newVerifyAuth);
			ThreadUtils.start(updater);
		}
		
//		int newCount = this.cli.getVerifyAuth().getAllGroupSpheres().size();
//		if(groupSpheresCount != 0 && groupSpheresCount!=newCount) {
//			this.cli.fireVerifyAuthChanged();
//		}
	}

	public String getProtocol() {
		return SSProtocolConstants.UPDATE_VERIFY;
	}

	public void handle(Hashtable update) {
		handleUpdateVerify(update);
	}

}
