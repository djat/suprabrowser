package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;


/**
 * TODO:#member-refactoring
 * 
 * 
 * This was actually working at one point. There were two ways to invite another
 * contact, either by clicking on their name and hitting an "invite" button.
 * That would set a status in their contact asset that there was a pending
 * invitation for that contact and would send an invitation via email (I think I
 * checked in invite.xml which was the text that was sent). If the recipient
 * person used the invite:: URL when logging in to welcomescreen with "invite"
 * argument supplied (the invite URL contained the sphere ID and the message I'd
 * of the contact), the system would then allow them to select a new username
 * and passphrase, which would then proceed to the normal user registration
 * code, i.e. create a new membership object and send new presence update to the
 * connected clients.
 * 
 * 
 */
public class AddInviteToContactHandler extends AbstractOldActionBuilder {

	private final DialogsMainCli cli;

	public AddInviteToContactHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_INVITE_TO_CONTACT;
	}

	@SuppressWarnings("unchecked")
	public void addInviteToContact(final Hashtable session,
			String contactMessageId, String inviteSphereId,
			String inviteSphereName, String inviteSphereType) {

		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.ADD_INVITE_TO_CONTACT);

		update.put(SessionConstants.SESSION, toSend);

		update.put(SessionConstants.CONTACT_MESSAGE_ID, contactMessageId);
		update.put(SessionConstants.INVITE_SPHERE_ID, inviteSphereId);
		update.put(SessionConstants.INVITE_SPHERE_NAME, inviteSphereName);
		update.put(SessionConstants.INVITE_SPHERE_TYPE, inviteSphereType);

		this.cli.sendFromQueue(update);

	}

}
