package ss.refactor.supraspheredoc.old.unused;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
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
public class RegisterSphereWithMembersHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RegisterSphereWithMembersHandler.class);
	
	private DialogsMainPeer peer;

	/**
	 * @deprecated
	 * @param peer
	 */
	public RegisterSphereWithMembersHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.REGISTER_SPHERE_WITH_MEMBERS;
	}

	public void handle(Hashtable update) {
		handleRegisterSphereWithMembers(update);
	}

	public void handleRegisterSphereWithMembers(final Hashtable update) {
		Vector members = (Vector) update.get(SC.MEMBERS);
		String system_name = (String) update.get(SC.SYSTEM_NAME2);//RC
		String display_name = (String) update.get(SC.DISPLAY_NAME);
		try {
			Document returnDoc = Utils.getUtils( this.peer )
					.registerSphereWithMembers(members, system_name,
							display_name);

			for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {

				handler.getVerifyAuth().setSphereDocument(returnDoc);
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_AUTH);
				dmpResponse.setVerifyAuthValue(SC.VERIFYAUTH, handler.getVerifyAuth());
				handler.sendFromQueue(dmpResponse);
			}
		} catch (DocumentException exc) {
			logger.error("Document Exception", exc);
		}
	}

}
