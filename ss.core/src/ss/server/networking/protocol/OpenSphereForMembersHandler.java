package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.domain.service.ICreateSphere;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.util.SessionConstants;

public class OpenSphereForMembersHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger.getLogger(OpenSphereForMembersHandler.class);

	private DialogsMainPeer peer;

	public OpenSphereForMembersHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.OPEN_SPHERE_FOR_MEMBERS;
	}

	public void handle(Hashtable update) {
		handleOpenSphereForMembers(update);
	}

	public void handleOpenSphereForMembers(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Vector membersDocs = (Vector) update.get(SC.MEMBERS);
		String system_name = (String) update.get(SC.SYSTEM_NAME2);// right
		// constant

		String display_name = (String) update.get(SC.DISPLAY_NAME);
		Document sphereDoc = (Document) update.get(SC.DOCUMENT);
		String username = (String) session.get(SC.USERNAME);
		String real_name = (String) session.get(SC.REAL_NAME);
		String sphereId = (String) session.get(SC.SPHERE_ID);
		String sphereURL = (String) session.get(SC.SPHERE_URL);
		String prefEmailAlias = (String) session.get(SessionConstants.EMAIL_ALIAS);
		if (logger.isDebugEnabled()) {
			logger.debug("Creating sphere " + display_name);
		}
		SupraSphereProvider.INSTANCE.get(this, ICreateSphere.class)
			.createSphere(membersDocs, system_name, display_name, sphereDoc,
				username, real_name, sphereId, sphereURL, prefEmailAlias);
	}

}
