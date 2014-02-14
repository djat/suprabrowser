/**
 * Jul 5, 2006 : 1:00:02 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.protocol.actions.AddInviteToContactHandler;
import ss.common.CreateMembership;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.VerifyAuth;
import ss.util.NameTranslation;
import ss.util.SessionConstants;

/**
 * 
 * TODO:#member-refactoring
 * 
 * Part of invitation process.
 * @see AddInviteToContactHandler
 */
public class InviteCompleteHandler implements ProtocolHandler {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InviteCompleteHandler.class);

	// TODO move
	private static final String MEMBER = "member";

	private static final String LOGIN_NAME = "login_name";

	private static final String VALUE = "value";

	private static final String _0000000000000000000 = "0000000000000000000";

	private static final String PROFILE_ID = "profile_id";

	private static final String MACHINE_VERIFIER = "machine_verifier";

	public static final String VERIFIER = "verifier";

	public static final String SALT = "salt";
	
	private final DialogsMainCli cli;

	public InviteCompleteHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	public void handleInviteComplete(final Hashtable update) {
		this.cli.setVeryfyAuthIfNull((VerifyAuth) update
				.get(SessionConstants.VERIFY_AUTH));

		Document invitingContactDoc = (Document) update
				.get(SessionConstants.INVITING_CONTACT_DOC);

		String salt = (String) update.get(SessionConstants.SALT);
		String verifier = (String) update.get(SessionConstants.VERIFIER);
		String username = (String) update.get(SessionConstants.USERNAME);

		CreateMembership membership = new CreateMembership();

		String cname = NameTranslation
				.createContactNameFromContactDoc(invitingContactDoc);
		Document memDoc = membership.createMember(cname, verifier, verifier);

		memDoc.getRootElement().element(VERIFIER).detach();

		memDoc.getRootElement().addElement(MACHINE_VERIFIER).addAttribute(SALT,
				salt).addAttribute(PROFILE_ID, _0000000000000000000).setText(
				verifier);

		memDoc.getRootElement().element(LOGIN_NAME).addAttribute(VALUE,
				username);

		// invitingContactDoc.getRootElement().element("current_sphere").detach();

		Hashtable sendSession = this.cli.getSF().getMainVerbosedSession().rawClone();
		String personalSphere = this.cli.getSF().client.getVerifyAuth()
				.getSystemName(
						(String) sendSession.get(SessionConstants.REAL_NAME));
		sendSession.put(SessionConstants.SPHERE_ID, personalSphere);
		invitingContactDoc.getRootElement().element("login").addAttribute(
				VALUE, username);
		this.cli.getSF().client.publishTerse(sendSession, memDoc);

		this.cli.getSF().client.publishTerse(sendSession, invitingContactDoc);		

		// logger.warn("registering..."+username+ " :
		// "+(String)sendSession.get("supra_sphere")+" :
		// "+(String)se);
		String sphereName = this.cli.getSF().client.getVerifyAuth()
				.getDisplayName((personalSphere));
		this.cli.getSF().client.registerMember(sendSession,
				(String) sendSession.get(SessionConstants.SUPRA_SPHERE),
				invitingContactDoc, (String) sendSession
						.get(SessionConstants.USERNAME), (String) sendSession
						.get(SessionConstants.REAL_NAME), sphereName,
				personalSphere, NameTranslation
						.createContactNameFromContactDoc(invitingContactDoc),
				username, MEMBER);
		/*
		 * public void registerMember(Hashtable session, String supraSphere,
		 * Document contactDoc, String inviteUsername, String inviteContact,
		 * String sphereName, String sphereId, String realName, String username,
		 * String inviteSphereType) {
		 */
	}

	public String getProtocol() {
		return SSProtocolConstants.INVITE_COMPLETE;
	}

	public void handle(Hashtable update) {
		handleInviteComplete(update);
	}

}
