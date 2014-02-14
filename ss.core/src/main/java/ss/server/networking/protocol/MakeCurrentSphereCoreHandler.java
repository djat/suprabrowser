package ss.server.networking.protocol;

import java.sql.SQLException;
import java.util.Hashtable;

import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

public class MakeCurrentSphereCoreHandler implements ProtocolHandler {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MakeCurrentSphereCoreHandler.class);

//	private static final String VALUE = "value";

//	private static final String LOGIN = "login";

	private DialogsMainPeer peer;

	public MakeCurrentSphereCoreHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.MAKE_CURRENT_SPHERE_CORE;
	}

	public void handle(Hashtable update) {
		handleMakeCurrentSphereCore(update);
	}

	public void handleMakeCurrentSphereCore(final Hashtable update) {
		final Hashtable finalSession = (Hashtable) update.get(SC.SESSION);
		//Document doc = (Document) update.get(SC.DOCUMENT);
		
		String sphereId = (String) finalSession.get(SC.SPHERE_ID);
		String supraSphere = (String) finalSession.get(SC.SUPRA_SPHERE);
		
		String displayName = this.peer.getVerifyAuth().getDisplayName(sphereId);

		String login = (String) update.get(SC.LOGIN);
		
		String contactName = (String) update.get(SC.CONTACT_NAME);

		XMLDB xmldb = this.peer.getXmldb();
		String loginSphere = xmldb.getUtils().getLoginSphereSystemName(login);
		
		Document membershipDoc = xmldb.getMembershipDoc(loginSphere, login);
		
		xmldb.insertDoc(membershipDoc, sphereId);
		try {
			xmldb.removeDoc(membershipDoc, loginSphere);
		} catch (SQLException ex) {
			logger.error("Can't remove membership doc", ex);
		}

		String sphereType = this.peer.getVerifyAuth().getSphereType(sphereId);

		Document returnSphereDoc = xmldb.makeCurrentSphereCore(supraSphere,
				login, sphereId, displayName, sphereType);
		
		this.peer.getVerifyAuth().setSphereDocument(returnSphereDoc);
		for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_VERIFY);
			handler.getVerifyAuth().setSphereDocument(returnSphereDoc);
			dmpResponse.setVerifyAuthValue(SC.VERIFY_AUTH, handler.getVerifyAuth());
			handler.sendFromQueue(dmpResponse);
		}
		DialogsMainPeer.sendForAllRefreshPresence(contactName, sphereId);
	}

}
