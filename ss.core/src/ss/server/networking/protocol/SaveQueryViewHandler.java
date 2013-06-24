package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.HandlerKey;

public class SaveQueryViewHandler implements ProtocolHandler {

	private static final String REPLACING_KEYWORD = "replacingKeyword";

	private static final String VALUE = "value";

	private static final String MULTI_LOC_SPHERE = "multi_loc_sphere";

	private static final String INTEREST = "interest";

	private static final String SEARCH = "search";

	private static final String MOMENT = "moment";

	private static final String CONTACT_NAME = "contact_name";

	private static final String RESPONSE_ID = "response_id";

	private static final String FROM_FIND_ASSETS = "from_find_assets";

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public SaveQueryViewHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SAVE_QUERY_VIEW;
	}

	public void handle(Hashtable update) {
		handleSaveQueryView(update);
	}

	public void handleSaveQueryView(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Element keywordElement = (Element) update.get(SC.KEYWORD_ELEMENT);
		Document doc = (Document) update.get(SC.DOCUMENT);
		
		String realName = (String) session.get(SC.REAL_NAME);
		String sphereId = (String) session.get(SC.SPHERE_ID);
		String multiLoc = (String) session.get(SC.MULTI_LOC_SPHERE);

		Element searchElem = doc.getRootElement().element(SEARCH);

		Element fromFindAssets = doc.getRootElement().element(FROM_FIND_ASSETS);
		Element responceElement = doc.getRootElement().element(RESPONSE_ID);
		if (fromFindAssets != null) {
			if (responceElement != null) {
				responceElement.detach();
			}
			fromFindAssets.detach();
		}
		String moment = DialogsMainPeer.getCurrentMoment();

		if (searchElem == null) {

			keywordElement.addAttribute(CONTACT_NAME, realName).addAttribute(
					MOMENT, moment);
			doc.getRootElement().addElement(SEARCH).addElement(INTEREST);
			doc.getRootElement().element(SEARCH).element(INTEREST).add(
					keywordElement);

			// logger.info("REPLACING HERE: "+doc.asXML());

			doc = this.peer.getXmldb().replaceDoc(doc, sphereId);

			if (doc != null) {
				doc = this.peer.getXmldb()
						.useDoc(doc, sphereId, realName, null);
			}

		} else {

			keywordElement.addAttribute(CONTACT_NAME, realName).addAttribute(
					MOMENT, moment);

			doc.getRootElement().element(SEARCH).element(INTEREST).add(
					keywordElement);

			this.logger.info("REPLACING HERE2: " + doc.asXML());
			String replaceSphereId = null;
			Element multiLocElement = doc.getRootElement().element(
					MULTI_LOC_SPHERE);
			if (multiLocElement != null) {
				replaceSphereId = multiLocElement.attributeValue(VALUE);
				multiLocElement.detach();
			} else {
				replaceSphereId = sphereId;
			}

			doc = this.peer.getXmldb().replaceDoc(doc, replaceSphereId);

			if (doc != null) {
				doc = this.peer.getXmldb()
						.useDoc(doc, sphereId, realName, null);
			}
			// xmldb.addQueryToContact((String)session.get("supra_sphere"),(String)session.get("contact_name"),keywordElement);

		}

		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_DOCUMENT);

		// System.out.println("UPDATE SPHERE ID:
		// "+(String)session.get("sphere_id"));
		// System.out.println("UPDATE STATUS:
		// "+doc.getRootElement().element("status").attributeValue("value"));

		dmpResponse.setStringValue(SC.SPHERE, sphereId);
		this.logger.info("my Doc ="+doc.asXML());
		doc.getRootElement().addElement(REPLACING_KEYWORD);

		// Date current = new Date();
		// String moment =
		// DateFormat.getTimeInstance(DateFormat.LONG).format(current)
		// + " " +
		// DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

		dmpResponse.setDocumentValue(SC.DOCUMENT, (Document) doc.clone());

		for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {
			String login = handler.get(HandlerKey.USERNAME);
			boolean sphereEnabled = handler.getVerifyAuth().isSphereEnabledForMember(sphereId,login);
			boolean multilockEnabled = multiLoc != null ? handler.getVerifyAuth().isSphereEnabledForMember(multiLoc,login) : false;
			if (multiLoc != null) {
				this.logger.warn("whenupdate: " + multiLoc + " : " + sphereId);
				dmpResponse.setStringValue(SC.SPHERE, multiLoc);
			} else {
				multilockEnabled = sphereEnabled;
			}
			dmpResponse.setMapValue(SC.SESSION, (Hashtable) session.clone());
			if (sphereEnabled||multilockEnabled) {
				this.logger.warn("it checked...send back...: " + doc.asXML());
				handler.sendFromQueue(dmpResponse);
			} else {
				this.logger.warn("when saving query view...could not send!! : "
						+ sphereId + " " + multiLoc );
			}
		}
	}

}
