package ss.server.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

public class VoteDocumentHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VoteDocumentHandler.class);

	private static final String SYSTEM_NAME = "system_name";

	private static final String SPHERE = "sphere";

	private static final String TYPE = "type";

	private static final String VALUE = "value";

	private static final String CURRENT_SPHERE = "current_sphere";

	private DialogsMainPeer peer;

	public VoteDocumentHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.VOTE_DOCUMENT;
	}

	public void handle(Hashtable update) {
		handleVoteDocument(update);
	}

	public void handleVoteDocument(final Hashtable update) {

		Thread t = new Thread() {
			private DialogsMainPeer peer = VoteDocumentHandler.this.peer;

			public void run() {
				Hashtable session = (Hashtable) update.get(SC.SESSION);
				Document doc = (Document) update.get(SC.DOCUMENT);

				String sphereId = (String) session.get(SC.SPHERE_ID);
				String real_name = (String) session.get(SC.REAL_NAME);
				String username = (String) session.get(SC.USERNAME);
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL,
						SSProtocolConstants.VOTE_DOCUMENT);
				dmpResponse.setStringValue(SC.SPHERE, sphereId);
				String data_sphere = this.peer.getXmldb().getUtils()
						.getInheritedName(sphereId);
				Document res = this.peer.getXmldb().voteDoc(doc, data_sphere,
						real_name);
				if (res != null) {

					if (res.getRootElement().element(CURRENT_SPHERE) != null) {
						res.getRootElement().element(CURRENT_SPHERE)
								.addAttribute(VALUE, sphereId);
					} else {
						res.getRootElement().addElement(CURRENT_SPHERE)
								.addAttribute(VALUE, sphereId);
					}
					dmpResponse.setDocumentValue(SC.DOCUMENT, res);

					for (DialogsMainPeer handler : DmpFilter
							.filter(sphereId)) {

						boolean addSphere = false;
						String type = doc.getRootElement().element(TYPE)
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
								logger.warn("did not equal!: " + systemSphereId + ", " + username);
							}
						} else {
							addSphere = true;
						}

						if (addSphere) {
							handler.sendFromQueue(dmpResponse);
						}
					}
					/*
					 * } catch (NullPointerException npe) {
					 * //System.out.println("NULL IN DOCUMENT"); }
					 */

				}
			}
		};
		t.start();
	}

}
