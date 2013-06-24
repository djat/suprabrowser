package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring 
 * Should be changed to smth 
 * 
 */
public class EntitleContactForOneSphereHandler extends AbstractOldActionBuilder {

	private final DialogsMainCli cli;

	public EntitleContactForOneSphereHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.ENTITLE_CONTACT_FOR_ONE_SPHERE;
	}

	@SuppressWarnings("unchecked")
	public void entitleContactForOneSphere(final Hashtable session,
			final Document contactDoc, final Document memberDoc,
			String existingMemberLogin, String existingMemberContact) {

		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();

		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.ENTITLE_CONTACT_FOR_ONE_SPHERE);
		update.put(SessionConstants.CONTACT_DOC, contactDoc);
		update.put(SessionConstants.MEMBER_DOC, memberDoc);

		update.put(SessionConstants.EXISTING_MEMBER_LOGIN, existingMemberLogin);
		update.put(SessionConstants.EXISTING_MEMBER_CONTACT,
				existingMemberContact);

		update.put(SessionConstants.SESSION, toSend);

		this.cli.sendFromQueue(update);

	}

}
