package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;

import ss.util.SessionConstants;

public class SaveQueryViewHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveQueryViewHandler.class);

	private final DialogsMainCli cli;
	
	public SaveQueryViewHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.SAVE_QUERY_VIEW;
	}

	@SuppressWarnings("unchecked")
	public void saveQueryView(final Hashtable session, Document sendDoc,
			Element keywordElement) {

		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SAVE_QUERY_VIEW);

		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, sendDoc);
		update.put(SessionConstants.KEYWORD_ELEMENT, keywordElement);

		logger.warn("document: " + sendDoc.asXML());
		logger.warn("keyword elem: " + keywordElement.asXML());
		logger.warn("before save query view: "
				+ (String) session.get(SessionConstants.SPHERE_ID2));

		this.cli.sendFromQueue(update);

	}

}
