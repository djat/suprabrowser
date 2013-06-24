package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.domainmodel.LoginSphere;
import ss.global.SSLogger;
import ss.server.domain.service.IReplaceUsernameInMembership;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class ReplaceUsernameInMembershipHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public ReplaceUsernameInMembershipHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.REPLACE_USERNAME_IN_MEMBERSHIP;
	}

	public void handle(Hashtable update) {
		handleReplaceUsernameInMembership(update);

	}

	public void handleReplaceUsernameInMembership(final Hashtable update) {
		this.logger.warn("got replace username in membership");
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		String oldUsername = (String) update.get(SC.OLD_USERNAME);
		String newUsername = (String) update.get(SC.NEW_USERNAME);
		String newSalt = (String) update.get(SC.NEW_SALT);
		String newVerifier = (String) update.get(SC.NEW_VERIFIER);
		
		String realName = (String) session.get(SC.REAL_NAME);
		String username = (String) session.get(SC.USERNAME);
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		String sSession = (String) session.get(SC.SESSION);

		this.logger.warn("new username :" + newUsername);
		
		replaceUsernameInMembership(session, oldUsername, newUsername, newSalt,
				newVerifier, realName, username, supraSphere, sSession);		
	}

	/**
	 * @param session
	 * @param oldUsername
	 * @param newUsername
	 * @param newSalt
	 * @param newVerifier
	 * @param realName
	 * @param username
	 * @param supraSphere
	 * @param sSession
	 */
	private void replaceUsernameInMembership(Hashtable session,
			String oldUsername, String newUsername, String newSalt,
			String newVerifier, String realName, String username,
			String supraSphere, String sSession) {
		final boolean isUserExist = this.peer.getVerifyAuth().isUserExist(
				newUsername);

		LoginSphere loginSphere = this.peer.getXmldb().getUtils()
				.findLoginSphereElement(oldUsername);

		boolean sameUsername = false;
		try {
			if (oldUsername.equals(newUsername)) {
				sameUsername = true;
			}
		} catch (NullPointerException exc) {
			this.logger.error("NPE ", exc);
		}
		boolean isAdmin = false;
		if (this.peer.getVerifyAuth().isAdmin(realName, username)) {
			isAdmin = true;
		}

		if (!isUserExist || sameUsername && isAdmin) {
			SupraSphereProvider.INSTANCE.get( this, IReplaceUsernameInMembership.class).replaceUsernameInMembership(session, oldUsername, newUsername,
					newSalt, newVerifier, username, supraSphere, sSession,
					loginSphere);
		}
	}

	

}
