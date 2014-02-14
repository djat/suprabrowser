package ss.refactor.supraspheredoc.old.unused;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.refactor.supraspheredoc.old.Utils;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

/**
 * @deprecated
 * @author dankosedin
 * 
 */
public class CrossreferenceSpheresHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	/**
	 * @deprecated
	 */
	public CrossreferenceSpheresHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.CROSSREFERENCE_SPHERES;
	}

	public void handle(Hashtable update) {
		handleCrossreferenceSpheres(update);
	}

	public void handleCrossreferenceSpheres(final Hashtable update) {
		Hashtable crossreference = (Hashtable) update.get(SC.CROSSREFERENCE);
		String cname = (String) update.get(SC.CONTACT_NAME);
		String lname = (String) update.get(SC.LOGIN);
		Hashtable decisiveUsers = (Hashtable) update.get(SC.DECISIVE_USERS);

		try {
			this.peer.getXmldb().getUtils().crossreferenceSpheres(
					cname, lname, crossreference, decisiveUsers);
			for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {

				handler.getVerifyAuth().setSphereDocument( Utils.getUtils(handler ).getSupraSphereDocument());
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_AUTH);
				dmpResponse.setVerifyAuthValue(SC.VERIFYAUTH, handler.getVerifyAuth());
				handler.sendFromQueue(dmpResponse);
			}
		} catch (NullPointerException exc) {
			this.logger.error("Document Exception", exc);
		}
	}

}
