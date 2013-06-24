/**
 * 
 */
package ss.client.ui;

import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.swt.graphics.Font;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.peoplelist.IPeopleListOwner;
import ss.common.UserSession;
import ss.common.VerifyAuth;
import ss.util.VotingEngine;

/**
 * @author zobo
 *
 */
public class PeopleTableOwner implements IPeopleListOwner {

	private final MessagesPane pane;
	
	public PeopleTableOwner( final MessagesPane pane ){
		this.pane = pane;
	}
	
	public Font getFont() {
		return this.pane.getFont();
	}

	public DialogsMainCli getClientProtocol() {
		return this.pane.client;
	}

	public boolean isContactEnabled(String memberName){
		final VerifyAuth verifyAuth = this.pane.client
			.getVerifyAuth();
		final UserSession userSession = this.pane
			.getUserSession();
		final boolean isContactEnabled = verifyAuth
			.isCurentSphereEnabledForContact(userSession, memberName);
		return isContactEnabled;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.peoplelist.IPeopleListOwner#getVotingEngine()
	 */
	public VotingEngine getVotingEngine() {
		return this.pane.getVotingEngine();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.peoplelist.IPeopleListOwner#getLastSelectedDoc()
	 */
	public Document getLastSelectedDoc() {
		return this.pane.getLastSelectedDoc();
	}
	
	public boolean isRoot(){
		//return false;  - SHOULD BE
		return this.pane.isRootView();
	}
	
	public String getSphereIdForUserActivity(){
		return this.pane.getSphereId();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.peoplelist.IPeopleListOwner#selectInPane(java.lang.String)
	 */
	public void selectInPane(String messageId) {
		this.pane.getMessagesTree().selectMessage(messageId);
		this.pane.selectItemInTable(messageId);
	}
	
	public Hashtable getSession(){
		return this.pane.getRawSession();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.peoplelist.IPeopleListOwner#getMessagesPane()
	 */
	public MessagesPane getMessagesPane() {
		return this.pane;
	}
}
