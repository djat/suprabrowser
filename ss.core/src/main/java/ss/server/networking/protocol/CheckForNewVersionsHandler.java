package ss.server.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

public class CheckForNewVersionsHandler implements ProtocolHandler {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CheckForNewVersionsHandler.class);

	private DialogsMainPeer peer;

	public CheckForNewVersionsHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.CHECK_FOR_NEW_VERSIONS;
	}

	public void handle(Hashtable update) {
		handleCheckForNewVersions(update);
	}

	public void handleCheckForNewVersions(final Hashtable update) {
		try {
			Thread.sleep(250);
		} catch (InterruptedException ex) {
		}
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document versionsDoc = (Document) update.get(SC.VERSIONS_DOC);
		Document toDownload = this.peer.getXmldb().getUtils()
				.checkAgainstCurrentVersions(versionsDoc);
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.CHECK_FOR_NEW_VERSIONS);
		dmpResponse.setDocumentValue(SC.TO_DOWNLOAD, toDownload);
		dmpResponse.setMapValue(SC.SESSION, session);
		this.peer.sendFromQueue(dmpResponse);
	}

}
