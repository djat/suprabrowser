package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.VerifyAuth;
import ss.global.SSLogger;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;

public class SaveWindowPositionToContactHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public SaveWindowPositionToContactHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SAVE_WINDOW_POSITION_TO_CONTACT;
	}

	public void handle(Hashtable update) {
		handleSaveWindowPositionToContact(update);
	}

	public void handleSaveWindowPositionToContact(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);		
		Document buildDoc = (Document) update.get(SC.DOCUMENT);
				
		String username = (String) session.get(SC.USERNAME);

		try {
			String loginSphere = this.peer.getVerifyAuth().getLoginSphere(
					username);
			Document newContactDoc = this.peer
					.getXmldb()
					.saveWindowPositionToContact(session, buildDoc, loginSphere);

			final VerifyAuth verifyAuth = new VerifyAuth(session);
			this.peer.setVerifyAuth(verifyAuth);
			SupraSphereProvider.INSTANCE.configureVerifyAuth(verifyAuth);
			this.peer.getVerifyAuth().setContactDocument(newContactDoc);

			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_VERIFY);
			dmpResponse.setMapValue(SC.SESSION, session);
			dmpResponse.setVerifyAuthValue(SC.VERIFY_AUTH, this.peer.getVerifyAuth());
			FilteredHandlers filteredHandlers = FilteredHandlers
					.getUserAllHandlersFromSession(session);
			for (DialogsMainPeer handler : filteredHandlers) {
				handler.sendFromQueue(dmpResponse);
			}
		} catch (NullPointerException exc) {
			this.logger.error("NPE Exception", exc);
		}
	}

}
