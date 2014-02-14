package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class AddInviteToContactHandler implements ProtocolHandler {

	/*
	 * private static final String SYSTEM_NAME = "system_name";
	 * 
	 * private static final String VALUE = "value";
	 * 
	 * private static final String LOGIN = "login";
	 */

	private DialogsMainPeer peer;

	public AddInviteToContactHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_INVITE_TO_CONTACT;
	}

	public void handle(Hashtable update) {
		handleAddInviteToContact(update);
	}

	public void handleAddInviteToContact(final Hashtable update) {
		Thread t = new Thread() {

			private DialogsMainPeer peer = AddInviteToContactHandler.this.peer;

			private Logger logger = SSLogger.getLogger(this.getClass());

			public void run() {

				Hashtable session = (Hashtable) update.get(SC.SESSION);
				String contactMessageId = (String) update
						.get(SC.CONTACT_MESSAGE_ID);
				String inviteSphereId = (String) update
						.get(SC.INVITE_SPHERE_ID);
				String inviteSphereName = (String) update
						.get(SC.INVITE_SPHERE_NAME);
				String inviteSphereType = (String) update
						.get(SC.INVITE_SPHERE_TYPE);

				String inviteSSName = (String) session.get(SC.SUPRA_SPHERE);

				this.logger.info("Adding invite to contact : "
						+ contactMessageId + " inviteSphereId: "
						+ inviteSphereId + " : " + inviteSphereName + " : "
						+ inviteSSName);

				Document contact = this.peer.getXmldb().addInviteToContact(
						session, contactMessageId, inviteSphereId,
						inviteSphereName, inviteSSName, inviteSphereType);

				this.peer.updateAllLocations(contact, session);

				// this block commented by Denis.
				// Reason : contactDoc, loginName,loginSphere, membershipDoc not
				// used
				/*
				 * Document contactDoc = this.peer.getXmldb().getSpecificID(
				 * inviteSphereId, contactMessageId);
				 * 
				 * String loginName = contactDoc.getRootElement().element(LOGIN)
				 * .attributeValue(VALUE);
				 * 
				 * boolean existsAndNotBlank = false; if (loginName != null) {
				 * if (loginName.length() > 0) { existsAndNotBlank = true; }
				 *  }
				 * 
				 * if (existsAndNotBlank == true) {
				 * 
				 * Document membershipDoc = this.peer.getXmldb()
				 * .getMembershipDoc(inviteSphereId, loginName); if
				 * (membershipDoc != null) {
				 *  //
				 * membershipDoc.getRootElement().addElement("change_passphrase_next_login"); //
				 * xmldb.replaceDoc(membershipDoc,inviteSphereId);
				 *  } else {
				 *  }
				 * 
				 * String loginSphere = this.peer.getXmldb().getLoginSphere(
				 * loginName).attributeValue(SYSTEM_NAME);
				 * 
				 * membershipDoc = this.peer.getXmldb().getMembershipDoc(
				 * loginSphere, loginName); if (membershipDoc != null) { //
				 * membershipDoc.getRootElement().addElement("change_passphrase_next_login");
				 *  // xmldb.replaceDoc(membershipDoc,loginSphere); } Document
				 * contact = this.peer.getXmldb().addInviteToContact( session,
				 * contactMessageId, inviteSphereId, inviteSphereName,
				 * inviteSSName, inviteSphereType);
				 * 
				 * this.peer.updateAllLocations(contact, session);
				 *  } else {
				 * 
				 * Document contact = this.peer.getXmldb().addInviteToContact(
				 * session, contactMessageId, inviteSphereId, inviteSphereName,
				 * inviteSSName, inviteSphereType);
				 * this.peer.updateAllLocations(contact, session); }
				 */

			}
		};
		t.start();
	}

}
