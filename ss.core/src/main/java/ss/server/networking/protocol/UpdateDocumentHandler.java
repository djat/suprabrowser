package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.domainmodel.Statement;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

public class UpdateDocumentHandler implements ProtocolHandler {

	private static final String EX_SYSTEM = "ex_system";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String VALUE = "value";

	private static final String CONFIRMED = "confirmed";

	private static final String LOCATIONS = "locations";

	private DialogsMainPeer peer;

	private static final Logger logger = SSLogger.getLogger(UpdateDocumentHandler.class);

	public UpdateDocumentHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.UPDATE_DOCUMENT;
	}

	public void handle(Hashtable update) {
		handleUpdateDocument(update);
	}

	public void handleUpdateDocument(final Hashtable update) {
		logger.warn("------------- Got update document");
		Thread t = new Thread() {
			private Logger logger = SSLogger.getLogger(this.getClass());

			private DialogsMainPeer peer = UpdateDocumentHandler.this.peer;

			@Override
			@SuppressWarnings("unchecked")
			public void run() {
				Hashtable session = (Hashtable) update.get(SC.SESSION);
				Document doc = (Document) update.get(SC.DOCUMENT);
				
				String sphereId = (String) session.get(SC.SPHERE_ID);
				String real_name = (String) session.get(SC.REAL_NAME);
				
				final boolean isKeywords = Statement.wrap( doc ).isKeywords(); 

				Vector locations = null;
				if (doc.getRootElement().element(LOCATIONS) != null) {
					locations = new Vector(doc.getRootElement().element(
							LOCATIONS).elements());
				}

				if (locations == null) {
					Document res = null;
					this.logger.warn("here we go..........................");
					
						String data_sphere = this.peer.getXmldb().getUtils().getInheritedName(
								sphereId);
						res = this.peer.getXmldb().voteDoc(doc,
								data_sphere, real_name);

						if (res.getRootElement().element(CONFIRMED).attributeValue(
								VALUE).equals("false")) {
							this.logger.warn("IT WAS NOT ACKNOWLEDGED HERE");
						}
						res.getRootElement().addElement(CURRENT_SPHERE)
								.addAttribute(VALUE, sphereId);
					
					final DmpResponse dmpResponse = new DmpResponse();
					dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_DOCUMENT);
					dmpResponse.setStringValue(SC.SPHERE, sphereId);					
					dmpResponse.setDocumentValue(SC.DOCUMENT, res);

					if ( isKeywords ) {
						DmpFilter.sendToAll(dmpResponse);
					}
					else {
						DmpFilter.sendToMembers(dmpResponse, sphereId);
					}
				} else {
					this.logger.info("sending to locaitons...");

					for (int i = 0; i < locations.size(); i++) {

						Element loc = (Element) locations.get(i);
						String locSphereId = loc.attributeValue(EX_SYSTEM);

						this.logger.info("Sending to this loc sphere id: "
								+ locSphereId);
						String data_sphere = this.peer.getXmldb().getUtils()
								.getInheritedName(locSphereId);
						Document res = this.peer.getXmldb().voteDoc(doc,
								data_sphere, real_name);
						res.getRootElement().addElement(CURRENT_SPHERE)
								.addAttribute(VALUE, locSphereId);
						
						final DmpResponse dmpResponse = new DmpResponse();
						dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_DOCUMENT);
						dmpResponse.setStringValue(SC.SPHERE, locSphereId);						
						dmpResponse.setDocumentValue(SC.DOCUMENT, res);

						if ( isKeywords ) {
							DmpFilter.sendToAll(dmpResponse);
						}
						else {
							DmpFilter.sendToMembers(dmpResponse, locSphereId);
						}
					}

				}
			}
		};
		t.start();
	}

}
