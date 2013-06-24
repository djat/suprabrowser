/**
 * 
 */
package ss.client.ui.root;

import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.swt.graphics.Font;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.peoplelist.IPeopleListOwner;
import ss.common.VerifyAuth;
import ss.util.VotingEngine;

/**
 * @author zobo
 *
 */
public class RootPeopleListOwner implements IPeopleListOwner {

	private final VotingEngine vouting;
	
	public RootPeopleListOwner(){
		this.vouting = new VotingEngine();
	}
	
	public DialogsMainCli getClientProtocol() {
		return SupraSphereFrame.INSTANCE.client;
	}

	public Font getFont() {
		return SupraSphereFrame.INSTANCE.getShell().getFont();
	}

	public Document getLastSelectedDoc() {
		return null;
	}

	public MessagesPane getMessagesPane() {
		return null;
	}

	public Hashtable getSession() {
		return SupraSphereFrame.INSTANCE.client.session;
	}

	public String getSphereIdForUserActivity() {
		return (String) SupraSphereFrame.INSTANCE.client.session.get("supra_sphere");
	}

	public VotingEngine getVotingEngine() {
		return this.vouting;
	}

	public boolean isContactEnabled(String memberName) {
		final VerifyAuth verifyAuth = getClientProtocol().getVerifyAuth();
		if ( verifyAuth != null ) {
			final String ownerContactName = verifyAuth.getUserSession().getRealName(); 
			final String personalSphereId = verifyAuth.getSphereSystemNameByContactAndDisplayName( memberName, ownerContactName );
			final String ownerUserLogin = verifyAuth.getUserSession().getUserLogin();
			return verifyAuth.isSphereEnabledForMember(personalSphereId, ownerUserLogin );
		}
		return false;
	}


	public boolean isRoot() {
		return true;
	}


	public void selectInPane(String messageId) {
		// NONE TO DO
	}

}
