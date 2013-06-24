/**
 * 
 */
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

/**
 * @author roman
 *
 */
public class AddSphereToContactFavouritesHandler implements ProtocolHandler {

	private DialogsMainPeer peer;
	
	private static final Logger logger = SSLogger.getLogger(AddSphereToContactFavouritesHandler.class);
	
	public AddSphereToContactFavouritesHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}
	
	public String getProtocol() {
		return SSProtocolConstants.ADD_SPHERE_TO_FAVOURITES;
	}

	public void handle(Hashtable update) {
		handleAddSphereToContactFavourites(update);

	}
	
	public void handleAddSphereToContactFavourites(Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document buildDoc = (Document) update.get(SC.DOCUMENT);

		String username = (String) session.get(SC.USERNAME);
		
		try {

			String loginSphere = this.peer.getVerifyAuth().getLoginSphere(
					username);

			logger.info("here is the build doc: " + buildDoc.asXML());

			Document newContactDoc = this.peer.getXmldb()
					.addSphereToContactFavourites(session, buildDoc, loginSphere);

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
			logger.info("NPE Exception", exc);
		}
	}

}
