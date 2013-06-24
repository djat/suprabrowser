package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.HandlerKey;

public class UseDocumentHandler implements ProtocolHandler {

	private static final String SYSTEM_NAME = "system_name";

	private static final String SPHERE = "sphere";

	private static final String VALUE = "value";

	private static final String CURRENT_SPHERE = "current_sphere";

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public UseDocumentHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.USE_DOCUMENT;
	}

	public void handle(Hashtable update) {
		handleUseDocument(update);
	}

	public void handleUseDocument(final Hashtable update) {
		this.logger.info("GOT USE ON PEER");

		Thread t = new Thread() {
			private Logger logger = SSLogger.getLogger(this.getClass());

			private DialogsMainPeer peer = UseDocumentHandler.this.peer;

			public void run() {
				Hashtable session = (Hashtable) update.get(SC.SESSION);
				Document doc = (org.dom4j.Document) update.get(SC.DOCUMENT);
				String increment = (String) update.get(SC.INCREMENT);
				
				String sphereId = (String) session.get(SC.SPHERE_ID);
				String real_name = (String) session.get(SC.REAL_NAME);
				String username = (String) session.get(SC.USERNAME);

				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.USE_DOCUMENT);
				dmpResponse.setStringValue(SC.SPHERE, sphereId);
				String data_sphere = this.peer.getXmldb().getUtils().getInheritedName(
						sphereId);
				Document res = this.peer.getXmldb().useDoc(doc, data_sphere,
						real_name, increment);
				if (res != null) {

					res.getRootElement().addElement(CURRENT_SPHERE)
							.addAttribute(VALUE, sphereId);
					dmpResponse.setDocumentValue(SC.DOCUMENT, res);

					for (DialogsMainPeer handler : DmpFilter
							.filter(sphereId)) {
						boolean addSphere = false;
						String type = doc.getRootElement().element("type")
								.attributeValue(VALUE);
						if (type.equals(SPHERE)) {
							String systemSphereId = doc.getRootElement()
									.attributeValue(SYSTEM_NAME);
							boolean check = handler.getVerifyAuth()
									.isSphereEnabledForMember(systemSphereId,
											username);
							if (check) {
								addSphere = true;
							} else {
								this.logger.warn("did not equal!: " + systemSphereId + ", " +
										username);
							}
						} else {
							addSphere = true;
						}

						if (addSphere) {
							handler.sendFromQueue(dmpResponse);
						}
						// }
					}
				}
			}
		};
		t.start();
	}

}
