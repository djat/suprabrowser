package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring
 * 
 * Main member registration function.
 * @see MethodProcessing.doRegisterImpl 
 *  
 * Should be changed to RegisterMemberAction 
 * 
 */
public class RegisterMemberHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RegisterMemberHandler.class);

	private final DialogsMainCli cli;
	
	public RegisterMemberHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.REGISTER_MEMBER;
	}

	@SuppressWarnings("unchecked")
	public void registerMember(Hashtable session, String supraSphere,
			Document contactDoc, String inviteUsername, String inviteContact,
			String sphereName, String sphereId, String realName,
			String username, String inviteSphereType) {

		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.REGISTER_MEMBER);
		update.put(SessionConstants.CONTACT_DOC, contactDoc);
		update.put(SessionConstants.INVITE_USERNAME, inviteUsername);
		update.put(SessionConstants.INVITE_CONTACT, inviteContact);
		update.put(SessionConstants.SPHERE_NAME2, sphereName);// RC
		update.put(SessionConstants.SPHERE_ID, sphereId);// RC
		update.put(SessionConstants.REAL_NAME2, realName);// RC
		update.put(SessionConstants.USERNAME, username);
		update.put(SessionConstants.INVITE_SPHERE_TYPE, inviteSphereType);
		update.put(SessionConstants.SESSION, toSend);
		this.cli.sendFromQueue(update);
	}

}
