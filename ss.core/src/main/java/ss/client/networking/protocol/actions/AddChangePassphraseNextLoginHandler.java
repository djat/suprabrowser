package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring 
 * TBI#ChangePassphraseNextLoginTransaction
 * 
 * Compare with ChangePassphraseNextLoginHandler 
 * 
 */
public class AddChangePassphraseNextLoginHandler extends AbstractOldActionBuilder {

	private final DialogsMainCli cli;

	public AddChangePassphraseNextLoginHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_CHANGE_PASSPHRASE_NEXT_LOGIN;
	}

	@SuppressWarnings("unchecked")
	public void addChangePassphraseNextLogin(Hashtable session, String login) {

		Hashtable toSend = new Hashtable();

		//TODO not needed. server dont check it.
		toSend.put(SessionConstants.SESSION, session);
		
		toSend.put(SessionConstants.LOGIN, login);		
		toSend.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.ADD_CHANGE_PASSPHRASE_NEXT_LOGIN);
		this.cli.sendFromQueue(toSend);

	}

}
