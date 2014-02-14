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
public class RemoveSphereFromContactFavouritesHandler implements ProtocolHandler {

	private DialogsMainPeer peer;
	
	private static final Logger logger = SSLogger.getLogger(AddSphereToContactFavouritesHandler.class);
	
	public RemoveSphereFromContactFavouritesHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}
	
	public String getProtocol() {
		return SSProtocolConstants.REMOVE_FROM_FAVORITES;
	}

	
	public void handle(Hashtable update) {
		handleRemoveSphereFromContactFavourites(update);
	}
	
	public void handleRemoveSphereFromContactFavourites(Hashtable update) {
		
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		String username = (String) session.get(SC.USERNAME);
		String id = (String) update.get("toRemove");

		try {

			String loginSphere = this.peer.getVerifyAuth().getLoginSphere(
					username);

			Document newContactDoc = this.peer.getXmldb()
					.removeSphereFromContactFavourites(session, id, loginSphere);

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
