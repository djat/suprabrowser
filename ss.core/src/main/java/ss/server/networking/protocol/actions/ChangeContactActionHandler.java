/**
 * 
 */
package ss.server.networking.protocol.actions;

import ss.client.networking.protocol.actions.ChangeContactAction;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.server.functions.changecontact.GloballyChangeAllTheContact;
import ss.server.functions.changecontact.GloballyChangeUsersContactName;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class ChangeContactActionHandler extends
		AbstractActionHandler<ChangeContactAction> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChangeContactActionHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public ChangeContactActionHandler( DialogsMainPeer peer ) {
		super(ChangeContactAction.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute(ChangeContactAction action) {
		if (logger.isDebugEnabled()) {
			logger.debug("TotallyChangeContactNameActionHandler execute started");
		}
		perform( action.getOldContactName(), action.getNewContactNameFirstName(), action.getNewContactNameLastName(), action.getNewContact() );
		if (logger.isDebugEnabled()) {
			logger.debug("TotallyChangeContactNameActionHandler execute finished");
		}
	}

	/**
	 * @param oldContactName
	 * @param contactStatement 
	 * @param newContactName
	 */
	private void perform(final String oldContactName, final String newFirstName, final String newLastName, final ContactStatement contactStatement) {
		if ( StringUtils.isBlank(oldContactName) ) {
			logger.error("oldContactName is blank");
			return;
		}
		if ( contactStatement != null ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Changing globally fully contact");
			}
			GloballyChangeAllTheContact function = new GloballyChangeAllTheContact( this.peer );
			function.perform(oldContactName, contactStatement);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Changing globally only contact name");
			}
			if ( StringUtils.isBlank(newFirstName) ) {
				logger.error("newFirstName is blank");
				return;
			}
			if ( StringUtils.isBlank(newLastName) ) {
				logger.error("newLastName is blank");
				return;
			}
			GloballyChangeUsersContactName function = new GloballyChangeUsersContactName( this.peer );
			function.perform(oldContactName, newFirstName, newLastName);
		}
	}
}
