/**
 * 
 */
package ss.server.functions.changecontact;

import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GloballyChangeAllTheContact extends ChangeContactAbstractFunction {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GloballyChangeAllTheContact.class);
	
	/**
	 * @param peer
	 */
	public GloballyChangeAllTheContact(DialogsMainPeer peer) {
		super(peer);
	}

	public void perform( final String oldContactName, final ContactStatement contact ){
		final String newContactName = contact.getContactNameByFirstAndLastNames();
		final String login = getPeer().getVerifyAuth().getLoginForContact(oldContactName);
		if (logger.isDebugEnabled()) {
			logger.debug("Starting renaming. Login: " + login);
			logger.debug("oldContactName: " + oldContactName);
			logger.debug("newContactName: " + newContactName);
		}
		if ( StringUtils.isNotBlank(login) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("This is existing user");
			}
			replaceAllInfoInContacts(oldContactName, contact);
			if ( !oldContactName.equals(newContactName) ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Contact name for user has been changed");
				}
				performContactNameForExistedUserChanged(login, oldContactName, newContactName);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("This is not existing user");
			}
			replaceAllInfoInContacts(oldContactName, contact);
		}
	}
	
	private void performContactNameForExistedUserChanged( final String login, 
			final String oldContactName, final String newContactName){
		replaceInMembership(login, newContactName);
		replaceInSphereDefinitions(oldContactName, newContactName);
		replaceInVoutingAndGiver(oldContactName, newContactName);
		AddChangesToSupraSphereDoc(login , oldContactName, newContactName);
		updateVerifyAuth();
	}
}
