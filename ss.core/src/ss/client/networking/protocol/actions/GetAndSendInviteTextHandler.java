/**
 * Jul 4, 2006 : 6:00:26 PM
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring 
 * Part of invitation process.
 * @see AddInviteToContactHandler 
 * 
 */
public class GetAndSendInviteTextHandler extends AbstractOldActionBuilder {

	private DialogsMainCli cli;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetAndSendInviteTextHandler.class);

	public GetAndSendInviteTextHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.GET_AND_SEND_INVITE_TEXT;
	}

	@SuppressWarnings("unchecked")
	public void getAndSendInviteText(Hashtable session, Document contactDoc,
			String fromDomain, String fromEmail) {
		Hashtable toSend = (Hashtable) session.clone();
		Hashtable temp = new Hashtable();
		temp.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.GET_AND_SEND_INVITE_TEXT);

		temp.put(SessionConstants.CONTACT_DOC, contactDoc);
		temp.put(SessionConstants.FROM_DOMAIN, fromDomain);
		temp.put(SessionConstants.FROM_EMAIL, fromEmail);
		temp.put(SessionConstants.SESSION, toSend);
		this.cli.sendFromQueue(temp);
	}

}
