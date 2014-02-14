package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring
 * 
 * change login in membership 
 * 
 * XmldbUtils.replaceUsernameInSupraSphereDoc 
 * XmldbUtils.replaceUsernameInMembership
 * and replace contact doc  
 * 
 */
public class ReplaceUsernameInMembershipHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReplaceUsernameInMembershipHandler.class);

	private final DialogsMainCli cli;
	
	public ReplaceUsernameInMembershipHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.REPLACE_USERNAME_IN_MEMBERSHIP;
	}

	@SuppressWarnings("unchecked")
	public void replaceUsernameInMembership(Hashtable session,
			String oldUsername, String newUsername, String newSalt,
			String newVerifier) {

		Hashtable toSend = new Hashtable();
		toSend.put(SessionConstants.OLD_USERNAME, oldUsername);
		toSend.put(SessionConstants.NEW_USERNAME, newUsername);
		toSend.put(SessionConstants.NEW_SALT, newSalt);
		toSend.put(SessionConstants.NEW_VERIFIER, newVerifier);
		toSend.put(SessionConstants.SESSION, session);
		toSend.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.REPLACE_USERNAME_IN_MEMBERSHIP);
		this.cli.sendFromQueue(toSend);
	}

}
