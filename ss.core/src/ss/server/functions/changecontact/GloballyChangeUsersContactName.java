/**
 * 
 */
package ss.server.functions.changecontact;

import ss.common.StringUtils;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GloballyChangeUsersContactName extends ChangeContactAbstractFunction {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GloballyChangeUsersContactName.class);
	/**
	 * @param peer
	 */
	public GloballyChangeUsersContactName(DialogsMainPeer peer) {
		super(peer);
	}

	public void perform( final String oldContactName, final String newFirstName, final String newLastName ){
		final String newContactName = newFirstName + " " + newLastName;
		final String login = getPeer().getVerifyAuth().getLoginForContact(oldContactName);
		if ( StringUtils.isBlank( login )) {
			logger.error("login is null, this is not a member");
			return;
		}
		if ( StringUtils.isNotBlank(getPeer().getVerifyAuth().getLoginForContact(newContactName)) ) {
			logger.error("user with such contact name already exists: " + newContactName);
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Starting renaming. Login: " + login);
			logger.debug("oldContactName: " + oldContactName);
			logger.debug("newContactName: " + newContactName);
		}
		replaceInMembership(login, newContactName);
		replaceInSphereDefinitions(oldContactName, newContactName);
		replaceContactNamesInContacts(oldContactName, newFirstName, newLastName);
		replaceInVoutingAndGiver(oldContactName, newContactName);
		AddChangesToSupraSphereDoc(login , oldContactName, newContactName);
		updateVerifyAuth();
	}
}
