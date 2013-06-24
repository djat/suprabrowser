package ss.server.networking.protocol;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.domain.service.ISupraSphereFacade;
import ss.global.SSLogger;
import ss.server.db.AcrossTableUtils;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.HandlerKey;

public class RemoveSphereHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final String VALUE = "value";

	@SuppressWarnings("unused")
	private static final String TYPE = "type";

	private static final String SYSTEM_NAME = "system_name";

	public static final String RECALL = "recall";

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public RemoveSphereHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.REMOVE_SPHERE;
	}

	public void handle(Hashtable update) {
		handleRemoveSphere(update);
	}

	public void handleRemoveSphere(final Hashtable update) {
		this.logger.warn("Got remove sphere");
		
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document doc = (Document) update.get(SC.DOCUMENT);
		
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		try {

			AcrossTableUtils across = new AcrossTableUtils();

			String sphereSystemName = doc.getRootElement().attributeValue(
					SYSTEM_NAME);
			Document supraSphereDoc = this.peer.getXmlDbOld()
					.getSupraSphereDocument();
//			Document cachedSSDoc = (Document) supraSphereDoc.clone();
			ISupraSphereFacade supraSphereFacade = this.peer.getVerifyAuth().getSupraSphere().duplicate();
			String xpath = DialogsMainPeer
					.createSphereIdApath(sphereSystemName);

			for (Object o : getSpheres(supraSphereDoc, xpath)) {
				((Element) o).detach();
			}

			Hashtable locations = across
					.getLocationsOfSphereSystemName(sphereSystemName);

			for (Enumeration enumer = locations.keys(); enumer
					.hasMoreElements();) {

				String sphereId = (String) enumer.nextElement();

				Document sphereDoc = (Document) locations.get(sphereId);
				this.peer.getXmldb().removeDoc(sphereDoc, sphereId);

				// mp.removeSphereFromMembers(session,
				this.logger.warn("removed from : " + sphereId);

				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.RECALL_MESSAGE);
				dmpResponse.setDocumentValue(SC.DOCUMENT, sphereDoc);
				dmpResponse.setStringValue(SC.SPHERE, sphereId);
				dmpResponse.setStringValue(SC.DELIVERY_TYPE, RECALL);

				for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {	
					String contact = handler.get(HandlerKey.USERNAME);
					if ( supraSphereFacade.isSphereEnabledForContact( sphereId, contact) ) {
						handler.sendFromQueue(dmpResponse);
					}					
				}
			}
			this.peer.getXmldb().replaceDoc(supraSphereDoc, supraSphere);
			this.peer.sendUpdateVerifyToAll(supraSphereDoc);

		} catch (SQLException exc) {
			this.logger.error("SQL exception while removing sphere", exc);
		} catch (DocumentException exc) {
			this.logger.error("Document Exception", exc);
		}
	}

	@SuppressWarnings("unchecked")
	private List getSpheres(Document supraSphereDoc, String xpath) {
		List list = new ArrayList();
		Object o = supraSphereDoc.selectObject(xpath);
		if (o != null) {
			if (o instanceof Element) {
				list.add(o);
			} else {
				list = (List) o;
			}
		}
		return list;
	}

}
