package ss.server.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class AddChangePassphraseNextLoginHandler implements ProtocolHandler {
	
	private static final String CHANGE_PASSPHRASE_NEXT_LOGIN = "change_passphrase_next_login";
	private DialogsMainPeer peer;

	public AddChangePassphraseNextLoginHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_CHANGE_PASSPHRASE_NEXT_LOGIN;
	}

	public void handle(Hashtable update) {
		handleAddChangePassphraseNextLogin(update);
	}

	public void handleAddChangePassphraseNextLogin(final Hashtable update) {
		//TODO some session checks?
		String loginName = (String) update.get(SC.LOGIN);
		
		String loginSphere = this.peer.getVerifyAuth().getLoginSphere(loginName);
	
		Document membershipDoc = this.peer.getXmldb().getMembershipDoc(loginSphere, loginName);
	
		if (membershipDoc != null) {
			membershipDoc.getRootElement().addElement(
					CHANGE_PASSPHRASE_NEXT_LOGIN);
			this.peer.getXmldb().replaceDoc(membershipDoc, loginSphere);
	
		}
	}

}
