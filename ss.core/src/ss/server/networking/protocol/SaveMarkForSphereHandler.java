package ss.server.networking.protocol;

import java.util.Hashtable;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class SaveMarkForSphereHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveMarkForSphereHandler.class);
	
	public static final String LOCAL_OR_GLOBAL = "localOrGlobal";

	private DialogsMainPeer peer;

	public SaveMarkForSphereHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SAVE_MARK_FOR_SPHERE;
	}

	public void handle(Hashtable update) {
		handleSaveMarkForSphere(update);
	}

	public void handleSaveMarkForSphere(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		String localOrGlobal = (String) update.get(LOCAL_OR_GLOBAL);
		
		String realName = (String) session.get(SC.REAL_NAME);
 		String sphereId = (String) session.get(SC.SPHERE_ID);
		String username = (String) session.get(SC.USERNAME);
		String personalSphere = this.peer.getVerifyAuth().getSystemName(
				realName);
		logger.info("personal sphere");
		this.peer.getXmldb().setMarkForSphere(personalSphere, sphereId,
				realName, username, localOrGlobal);
	}

}
