/**
 * 
 */
package ss.client.ui.peoplelist;

import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.swt.graphics.Font;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.util.VotingEngine;

/**
 * @author zobo
 *
 */
public interface IPeopleListOwner {
	public Font getFont();

	public DialogsMainCli getClientProtocol();
	
	public boolean isContactEnabled(String memberName);

	public VotingEngine getVotingEngine();

	public Document getLastSelectedDoc();
	
	public boolean isRoot();

	public String getSphereIdForUserActivity();

	public void selectInPane(String messageId);
	
	public Hashtable getSession();
	
	// For SearchWindow usage only.
	public MessagesPane getMessagesPane();
}
