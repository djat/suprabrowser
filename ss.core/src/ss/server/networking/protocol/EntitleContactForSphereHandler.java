package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.domain.service.IEntitleContactForSphere;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

//TODO It seem that sphereId, entitledSphereId and sphereId1 are mixed up.  
public class EntitleContactForSphereHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public EntitleContactForSphereHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.ENTITLE_CONTACT_FOR_SPHERE;
	}

	public void handle(Hashtable update) {
		handleEntitleContactForSphere(update);
	}

	@SuppressWarnings("unchecked")
	public void handleEntitleContactForSphere(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		String inviteSphereType = (String) update.get(SC.SPHERE_TYPE);
		Document contactDoc = (Document) update.get(SC.DOCUMENT);
		String tableId = (String) update.get(SC.TABLE_ID);

		String sphereId = (String) session.get(SC.SPHERE_ID);
		this.logger.info("Sphere type in entitle: " + inviteSphereType);
		SupraSphereProvider.INSTANCE.get(this, IEntitleContactForSphere.class)
			.entitleContactForSphere(session, inviteSphereType, contactDoc,
				tableId, sphereId);
	}

}
