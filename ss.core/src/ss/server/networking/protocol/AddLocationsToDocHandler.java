package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.util.XMLSchemaTransform;

public class AddLocationsToDocHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public AddLocationsToDocHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_LOCATIONS_TO_DOC;
	}

	public void handle(Hashtable update) {
		handleAddLocationsToDoc(update);
	}

	public void handleAddLocationsToDoc(final Hashtable update) {
		this.logger.info("got addlocaitons");
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document document = (Document) update.get(SC.DOCUMENT);
		String newSphereId = (String) update.get(SC.NEW_SPHERE_ID);
		String newSphereName = (String) update.get(SC.NEW_SPHERE_NAME);

		String sphereURL = (String) session.get(SC.SPHERE_URL);
		String sphereId = (String) session.get(SC.SPHERE_ID);
		String displayName = this.peer.getVerifyAuth().getDisplayName(sphereId);

		Document sendDoc = XMLSchemaTransform.addLocationToDoc(document,
				document, sphereURL, sphereId, displayName, newSphereId,
				newSphereName);

		this.peer.replaceAndUpdateAllLocations(session, sendDoc);
		
		// Document newDoc = (Document)document.clone();
		// newDoc.getRootElement().element("message_id").addAttribute("value",newMessageId);
		// // Doing this so it will be consistent with
		// addLocationToDoc method...

	}

}
