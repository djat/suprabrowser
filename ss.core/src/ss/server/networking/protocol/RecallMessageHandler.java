package ss.server.networking.protocol;

import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.XmlDocumentUtils;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

public class RecallMessageHandler implements ProtocolHandler {

	private static final String RECALL = "recall";

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public RecallMessageHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.RECALL_MESSAGE;
	}

	public void handle(Hashtable update) {
		handleRecallMessage(update);
	}

	public void handleRecallMessage(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document doc = (Document) update.get(SC.DOCUMENT);
		// String sphere = (String) update.get(SessionConstants.SPHERE_ID2);
		String sphere_id = (String) session.get(SC.SPHERE_ID);

		this.logger.info("recall start: "
				+ XmlDocumentUtils.toPrettyString(doc));

		try {
			this.peer.getXmldb().removeDoc(doc, sphere_id);
		} catch (NullPointerException npe) {
			this.logger.error("got null pointer", npe);
		} catch (SQLException exc) {
			this.logger.error("SQL Excepion in recallDoc", exc);

		}

		final DmpResponse dmpResponse = new DmpResponse();
		doc.getRootElement().addElement("current_sphere").addAttribute("value",
				sphere_id);
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.RECALL_MESSAGE);
		dmpResponse.setDocumentValue(SC.DOCUMENT, doc);
		dmpResponse.setStringValue(SC.SPHERE, sphere_id);
		dmpResponse.setStringValue(SC.DELIVERY_TYPE, RECALL);

		for (DialogsMainPeer handler : DmpFilter.filterOrAdmin(sphere_id)) {
			handler.sendFromQueue(dmpResponse);
		}
	}

}
